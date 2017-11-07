package rvc;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rvc.http.Request;
import rvc.http.Response;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;


/*TODO: Needs more improvements*/
/*TODO: Need to imlement ssl keys*/
public class RvcHandler extends ServletContextHandler {

    private static final Logger logger = LoggerFactory.getLogger(RvcHandler.class);

    RvcServer rvcServer;

    Map<String, RvcFileHandler> resourceHandlers;

    public RvcHandler(Server server) {
        super(server, "/", ServletContextHandler.SESSIONS);

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        }

    }

    public void setRvcServer(RvcServer rvcServer) {
        this.rvcServer = rvcServer;

        resourceHandlers = new HashMap<>();

        if (rvcServer.folders.isEmpty()) {
            return;
        }

        for (String key : rvcServer.folders.keySet()) {
            RvcFileHandler resourceHandler = new RvcFileHandler();
            resourceHandler.setMimeTypes(new MimeTypes());
            resourceHandler.setDirectoriesListed(false);
            resourceHandler.setResourceBase(rvcServer.folders.get(key));
            resourceHandlers.put(key, resourceHandler);
        }
    }

    void handleServlets(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException, ServletException {
        if ("/".equals(target)) return; // TODO: can't add "/" servlets :( .fix it
        ServletHandler handler = getServletHandler();
        Route r = new Route();
        for (ServletMapping servletMapping : handler.getServletMappings()) {
            for (String s : servletMapping.getPathSpecs()) {
                r.path(s);
                if (r.matchPath(target)) {
                    super.doHandle(target, baseRequest, servletRequest, servletResponse);
                    return;
                }
            }
        }
    }

    void handleResourceHandle(String serverName, String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException, ServletException {

        if ("/".equals(target)) {
            return;
        }

        RvcFileHandler resourceHandler = resourceHandlers.get(serverName);
        if (resourceHandler == null) {
            for (String key : resourceHandlers.keySet()) {
                if (Route.matchDomain(key, serverName)) {
                    resourceHandler = resourceHandlers.get(key);
                    break;
                }
            }
            if (resourceHandler == null) {
                resourceHandler = resourceHandlers.get(RvcServer.DEFAULT_DOMAIN);
            }
        }

        if (resourceHandler != null) {
            resourceHandler.handleGzip(target, baseRequest, servletRequest, servletResponse);
        }
    }

    @Override
    public void doHandle(String target, org.eclipse.jetty.server.Request baseRequest, HttpServletRequest servletRequest, HttpServletResponse servletResponse)
            throws IOException, ServletException {

        Response response = new Response(servletResponse);
        Request request = new Request(servletRequest);

        rvc.Context.set(Request.class, request);
        rvc.Context.set(Response.class, response);

        String serverName = servletRequest.getServerName();

        String method = servletRequest.getHeader("X-HTTP-Method-Override");
        if (method == null) {
            method = servletRequest.getMethod();
        }
        HttpMethod httpMethod = HttpMethod.valueOf(method.toUpperCase());

        String accept = servletRequest.getHeader("Accept");

        RouteContainer rc = rvcServer.routeContainer;

        Object content = null;

        try {
            //BEFORE filters
            filter(HttpMethod.BEFORE, target, serverName, accept);

            //TODO: kelgan urlni bazadan qarab, constant url bolsa,
            // TODO: osha url ni ochib qaraydigan action qilish kerak;

            if (request.isForwarded() || response.isRedirected()) {
                baseRequest.setHandled(true);
                return;
            }

            //servlet handler
            handleServlets(target, baseRequest, servletRequest, servletResponse);
            if (baseRequest.isHandled()) {
                return;
            }

            //resources handler
            handleResourceHandle(serverName, target, baseRequest, servletRequest, servletResponse);
            if (baseRequest.isHandled()) {
                return;
            }

            content = assignContent(content, response.content());

            Route route = rc.findMatchRoute(httpMethod, target, serverName, accept);
            if (route == null && httpMethod == HttpMethod.HEAD) {
                route = rc.findMatchRoute(HttpMethod.GET, target, serverName, accept);
            }
            if (route != null) {
                Request.get().setRoute(route);
                Object result;
                if (route.cacheExpire > 0) {
                    String url = Request.get().url2();
                    result = Cache.get(url);
                    if (result == null) {
                        result = route.action.handle();
                        Cache.put(url, result, route.cacheExpire);
                    }
                } else {
                    result = route.action.handle();
                }
                content = assignContent(content, result);
            } else {
                content = assignContent(content, error(HttpServletResponse.SC_NOT_FOUND, serverName));
            }

            //After filters
            filter(HttpMethod.AFTER, target, serverName, accept);
            content = assignContent(content, response.content());

        } catch (HaltException halt) {
            logger.debug("halting...");
            response.status(halt.getStatusCode());
            if (content == null && halt.getBody() == null) {
                content = "";
            } else {
                content = assignContent(content, halt.getBody());
            }
        } catch (Exception e) {
            try {
                Route route = rc.findMatchException(e.getClass(), serverName);
                if (route != null) {
                    content = assignContent(content, route.action.handle());
                } else {
                    throw e;
                }
            } catch (HaltException halt) {
                logger.debug("halting...");
                response.status(halt.getStatusCode());
                if (content == null && halt.getBody() == null) {
                    content = "";
                } else {
                    content = assignContent(content, halt.getBody());
                }
            } catch (Throwable throwable) {
                logger.error("Internal error exception", throwable);
                content = assignContent(content, error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, serverName));
            }

        } catch (Throwable throwable) {
            logger.error("Internal error exception", throwable);
            content = assignContent(content, error(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, serverName));
        }

        if (request.isForwarded() || response.isRedirected()) {
            baseRequest.setHandled(true);
            return;
        }

        if (servletResponse.getContentType() == null) {
            servletResponse.setContentType("text/html; charset=utf-8");
        }

        if (content == null) {
            content = error(404, serverName);
        }

        OutputStream outputStream = servletResponse.getOutputStream();
        outputStream = gzipWrapper(outputStream);
        if (content instanceof String) {
            outputStream.write(((String) content).getBytes("utf-8"));
        } else if (content instanceof byte[]) {
            outputStream.write((byte[]) content);
        } else if (content instanceof InputStream) {
            InputStream is = (InputStream) content;
            byte[] buffer = new byte[4096];
            int len = is.read(buffer);
            while (len != -1) {
                outputStream.write(buffer, 0, len);
                len = is.read(buffer);
            }
            is.close();
        } else {
            outputStream.write(String.valueOf(content).getBytes("utf-8"));
        }

        if (outputStream instanceof GZIPOutputStream) {
            outputStream.flush();
            outputStream.close();
        }

        baseRequest.setHandled(true);
    }

    private Object assignContent(Object content, Object result) {
        if (result == null) {
            return content;
        }
        return result;
    }

    private boolean resourceHandle(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (rvcServer.folders.isEmpty()) {
            return false;
        }

        return false;
    }

    private void filter(HttpMethod httpMethod, String target, String domain, String acceptType) throws Throwable {

        List<Route> matchRouteEntries =
                rvcServer.routeContainer.findMatchFilters(httpMethod, target, domain, acceptType);
        for (Route route : matchRouteEntries) {
            Request.get().setRoute(route);
            route.filter.handle();
        }
    }

    private Object error(int statusCode, String serverName) {
        Response.get().status(statusCode);
        ErrorPages.ErrorPage errorPage = rvcServer.errorPages.findMatch(statusCode, serverName);

        if (errorPage != null) {
            Route route = rvcServer.routeContainer.findMatchRoute(HttpMethod.GET, errorPage.page, serverName, RvcServer.DEFAULT_ACCEPT_TYPE);

            if (route != null) {
                try {
                    Request.get().setRoute(route);
                    return route.action.handle();
                } catch (Throwable throwable) {
                    logger.error("Internal error", throwable);
                    Response.get().status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return INTERNAL_ERROR;
                }
            } else {
                logger.warn("Assigned error page not found");
            }
        }

        switch (statusCode) {
            case HttpServletResponse.SC_NOT_FOUND:
                return String.format(NOT_FOUND, Request.get().path());
            case HttpServletResponse.SC_UNAUTHORIZED:
                return UNAUTHORIZED;
            case HttpServletResponse.SC_FORBIDDEN:
                return String.format(FORBIDDEN, Request.get().path());

            default:
                Response.get().status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return INTERNAL_ERROR;
        }

    }

    OutputStream gzipWrapper(OutputStream outputStream) {

        if (!Response.get().isGzip()) {
            return outputStream;
        }

        String gzipAccept = Request.get().header("Accept-Encoding");
        if (gzipAccept == null || !gzipAccept.contains("gzip")) {
            return outputStream;
        }

        try {
            outputStream = new GZIPOutputStream(outputStream, true);
            Response.get().header("Content-Encoding", "gzip");
        } catch (IOException e) {
            return outputStream;
        }

        return outputStream;

    }

    public static final String NOT_FOUND = "<!DOCTYPE html><html><head><title>404 Not found</title></head><body><h1>Not found</h1><p>The requested URL %s was not found on this server.</p></body></html>";
    public static final String INTERNAL_ERROR = "<!DOCTYPE html><html><head><title>500 Internal Error</title></head><body><h1>500 Internal Error</h1></body></html>";
    public static final String UNAUTHORIZED = "<!DOCTYPE html><html><head><title>401 Authorization Required</title></head><body><h1>401 Authorization Required</h1></body></html>";
    public static final String FORBIDDEN = "<!DOCTYPE html><html><head><title>403 Forbidden</title></head><body><h1>Forbidden</h1><p>You don't have permission to access %s on this server.</p></body></html>";
}