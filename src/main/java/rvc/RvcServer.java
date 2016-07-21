package rvc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rvc.ann.*;
import rvc.http.Response;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RvcServer {

    private static final Logger logger = LoggerFactory.getLogger(RvcServer.class);

    public static final int DEFAULT_PORT = 4567;
    public static final String DEFAULT_ACCEPT_TYPE = "*/*";
    public static final String DEFAULT_DOMAIN = "*";
    public static final String ALL_PATH = "*";
    public static final long DEFAULT_CACHE_LIFE = 0;

    public static Gson GSON = new GsonBuilder().setExclusionStrategies(new AnnotationSkipStrategy()).create();
    protected Map<Template.TemplateEngine, TemplateEngine> templateMap = new HashMap<>();

    protected int port = DEFAULT_PORT;
    protected String ip = "0.0.0.0";

    protected Ssl ssl;

    protected int maxThreads = -1;
    protected int minThreads = -1;
    protected int idleTimeoutMillis = -1;

    protected RouteContainer routeContainer = new RouteContainer();
    protected ErrorPages errorPages = new ErrorPages();
    protected Map<String, String> folders = new HashMap<>();

    protected Server server;

    public RvcHandler init() {

        if (maxThreads > 0) {
            int max = (maxThreads > 0) ? maxThreads : 200;
            int min = (minThreads > 0) ? minThreads : 8;
            int idleTimeout = (idleTimeoutMillis > 0) ? idleTimeoutMillis : 60000;
            server = new Server(new QueuedThreadPool(max, min, idleTimeout));
        } else {
            server = new Server();
        }

        ServerConnector connector = new ServerConnector(server);
        connector.setIdleTimeout(TimeUnit.HOURS.toMillis(1));
        connector.setSoLingerTime(-1);
        connector.setHost(ip);
        connector.setPort(port);

        server = connector.getServer();
        server.setConnectors(new Connector[]{connector});

        RvcHandler handler = new RvcHandler(server);
        handler.setRvcServer(this);
        return handler;
    }

    public RvcServer quickStart() {
//        init();
        try {
            server.start();
        } catch (Exception e) {
            logger.error("Starting the server failed.", e);
        }
        return this;
    }

    public RvcServer start() {
//        init();
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error("Starting the server failed.", e);
        }

        return this;
    }

    public RvcServer stop() throws Exception {
        server.stop();
        return this;
    }

    public RvcServer restart() {
        return this;
    }

    public Server getServer() {
        return server;
    }

    public RvcServer folder(String location) {
        folders.put(DEFAULT_DOMAIN, location);
        return this;
    }

    public RvcServer folder(String location, String domain) {
        folders.put(domain, location);
        return this;
    }

    public RvcServer secure(String keystoreFile, String keystorePassword,
                            String truststoreFile, String truststorePassword) {
        ssl = new Ssl(keystoreFile, keystorePassword,
                truststoreFile, truststorePassword);
        return this;
    }

    public RvcServer ip(String ip) {
        this.ip = ip;
        return this;
    }

    public RvcServer port(int port) {
        this.port = port;
        return this;
    }

    public RvcServer threads(int maxThreads, int minThreads, int idleTimeoutMillis) {
        this.maxThreads = maxThreads;
        this.minThreads = minThreads;
        this.idleTimeoutMillis = idleTimeoutMillis;
        return this;
    }

    public RvcServer page404(String page) {
        return errorPage(page, 404);
    }

    public RvcServer page404(String page, String domain) {
        return errorPage(page, 404, domain);
    }

    public RvcServer page401(String page) {
        return errorPage(page, 401);
    }

    public RvcServer page401(String page, String domain) {
        return errorPage(page, 401, domain);
    }

    public RvcServer page500(String page) {
        return errorPage(page, 500);
    }

    public RvcServer page500(String page, String domain) {
        return errorPage(page, 500, domain);
    }

    public RvcServer errorPage(String page, int code) {
        return errorPage(page, code, DEFAULT_DOMAIN);
    }

    public RvcServer errorPage(String page, int code, String domain) {
        errorPages.add(page, code, domain);
        return this;
    }

    public RvcServer get(String path, Action action, Object... params) {
        return get(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer get(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.GET, path, domain, action, params);
    }

    public RvcServer post(String path, Action action, Object... params) {
        return post(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer post(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.POST, path, domain, action, params);
    }

    public RvcServer put(String path, Action action, Object... params) {
        return put(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer put(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.PUT, path, domain, action, params);
    }

    public RvcServer delete(String path, Action action, Object... params) {
        return delete(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer delete(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.DELETE, path, domain, action, params);
    }

    public RvcServer head(String path, Action action, Object... params) {
        return head(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer head(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.HEAD, path, domain, action, params);
    }

    public RvcServer patch(String path, Action action, Object... params) {
        return patch(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer patch(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.PATCH, path, domain, action, params);
    }

    public RvcServer trace(String path, Action action, Object... params) {
        return trace(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer trace(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.TRACE, path, domain, action, params);
    }

    public RvcServer connect(String path, Action action, Object... params) {
        return connect(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer connect(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.CONNECT, path, domain, action, params);
    }

    public RvcServer options(String path, Action action, Object... params) {
        return options(path, DEFAULT_DOMAIN, action, params);
    }

    public RvcServer options(String path, String domain, Action action, Object... params) {
        return route(HttpMethod.OPTIONS, path, domain, action, params);
    }

    public RvcServer route(HttpMethod httpMethod, String path, String domain, final Action action, Object... params) {
        if ($.isEmpty(params)) {
            route(httpMethod, path, domain, action, DEFAULT_ACCEPT_TYPE, DEFAULT_CACHE_LIFE);
            return this;
        }

        long cacheExpire = DEFAULT_CACHE_LIFE;
        String acceptType = DEFAULT_ACCEPT_TYPE;

        Action actionNew = action;

        for (Object param : params) {
            if (param instanceof Integer) {
                cacheExpire = ((Integer) param).longValue();
                continue;
            }
            if (param instanceof Long) {
                cacheExpire = (long) param;
                continue;
            }
            if (param instanceof String) {
                String tmp = (String) param;
                if (tmp.split("/").length > 1)
                    acceptType = tmp;
                continue;
            }
            if (param instanceof TemplateEngine) {
                actionNew = () -> ((TemplateEngine) param).render(action.handle());
                continue;
            }
            if (param instanceof ResponseTransformer) {
                actionNew = () -> ((ResponseTransformer) param).transform(action.handle());
            }
        }

        route(httpMethod, path, domain, actionNew, acceptType, cacheExpire);
        return this;
    }

    private RvcServer route(HttpMethod httpMethod, String path, String domain, Action action, String acceptType, long cacheExpire) {
        if (path.contains(", ")) {
            String[] path2 = path.split(", ");
            for (String p : path2) {
                if (domain.contains(", ")) {
                    String[] domain2 = domain.split(" ,");
                    for (String d : domain2) {
                        routeContainer.addRoute(httpMethod, p, d, acceptType, cacheExpire, action);
                    }
                }
                routeContainer.addRoute(httpMethod, p, domain, acceptType, cacheExpire, action);
            }
        } else {
            routeContainer.addRoute(httpMethod, path, domain, acceptType, cacheExpire, action);
        }
        return this;
    }

    public RvcServer before(Filter filter) {
        return before(ALL_PATH, filter);
    }

    public RvcServer before(String path, Filter filter) {
        return before(path, DEFAULT_DOMAIN, filter);
    }

    public RvcServer before(String path, String domain, Filter filter) {
        return before(path, domain, DEFAULT_ACCEPT_TYPE, filter);
    }

    public RvcServer before(String path, String domain, String acceptType, Filter filter) {
        return filter(HttpMethod.BEFORE, path, domain, acceptType, filter);
    }

    public RvcServer after(Filter filter) {
        return after(ALL_PATH, filter);
    }

    public RvcServer after(String path, Filter filter) {
        return after(path, DEFAULT_DOMAIN, filter);
    }

    public RvcServer after(String path, String domain, Filter filter) {
        return after(path, domain, DEFAULT_ACCEPT_TYPE, filter);
    }

    public RvcServer after(String path, String domain, String acceptType, Filter filter) {
        return filter(HttpMethod.AFTER, path, domain, acceptType, filter);
    }

    public RvcServer filter(HttpMethod httpMethod, String path, String domain, String acceptType, Filter filter) {
        if (path.contains(", ")) {
            String[] path2 = path.split(", ");
            for (String p : path2) {
                if (domain.contains(", ")) {
                    String[] domain2 = domain.split(" ,");
                    for (String d : domain2) {
                        routeContainer.addFilter(httpMethod, p, d, acceptType, filter);
                    }
                }
                routeContainer.addFilter(httpMethod, p, domain, acceptType, filter);
            }
        } else {
            routeContainer.addFilter(httpMethod, path, domain, acceptType, filter);
        }
        return this;
    }

    public RvcServer exception(Class<? extends Exception> exceptionClass, Action action) {
        return exception(exceptionClass, DEFAULT_DOMAIN, action);
    }

    public RvcServer exception(Class<? extends Exception> exceptionClass, String domain, Action action) {
        routeContainer.addException(exceptionClass, domain, action);
        return this;
    }

    RvcServer classes(Class... aClasses) {
        try {
            for (Class aClass : aClasses) {
                if (aClass.isAnnotationPresent(Controller.class)) {
                    controllerClass(aClass);
                }
            }
        } catch (Exception e) {
            logger.error("Error during initialisation class. ", e);
        }
        return this;
    }

    private void controllerClass(Class aClass) throws Exception {
        Object controller = aClass.newInstance();
        String uri = controller.getClass().getAnnotation(Controller.class).url();
        for (Method method : aClass.getDeclaredMethods()) {
            String path;
            if (method.isAnnotationPresent(GET.class)) {
                GET get = method.getAnnotation(GET.class);
                path = get.absolutePath() ? get.value() : uri + get.value();
                methodClass(path, controller, method, HttpMethod.GET);
            }
            if (method.isAnnotationPresent(POST.class)) {
                POST post = method.getAnnotation(POST.class);
                path = post.absolutePath() ? post.value() : uri + post.value();
                methodClass(path, controller, method, HttpMethod.POST);
            }
            if (method.isAnnotationPresent(PUT.class)) {
                PUT put = method.getAnnotation(PUT.class);
                path = put.absolutePath() ? put.value() : uri + put.value();
                methodClass(path, controller, method, HttpMethod.PUT);
            }
            if (method.isAnnotationPresent(DELETE.class)) {
                DELETE delete = method.getAnnotation(DELETE.class);
                path = delete.absolutePath() ? delete.value() : uri + delete.value();
                methodClass(path, controller, method, HttpMethod.DELETE);
            }
            if (method.isAnnotationPresent(HEAD.class)) {
                HEAD head = method.getAnnotation(HEAD.class);
                path = head.absolutePath() ? head.value() : uri + head.value();
                methodClass(path, controller, method, HttpMethod.HEAD);
            }
            if (method.isAnnotationPresent(TRACE.class)) {
                TRACE trace = method.getAnnotation(TRACE.class);
                path = trace.absolutePath() ? trace.value() : uri + trace.value();
                methodClass(path, controller, method, HttpMethod.TRACE);
            }
            if (method.isAnnotationPresent(CONNECT.class)) {
                CONNECT connect = method.getAnnotation(CONNECT.class);
                path = connect.absolutePath() ? connect.value() : uri + connect.value();
                methodClass(path, controller, method, HttpMethod.CONNECT);
            }
            if (method.isAnnotationPresent(OPTIONS.class)) {
                OPTIONS options = method.getAnnotation(OPTIONS.class);
                path = options.absolutePath() ? options.value() : uri + options.value();
                methodClass(path, controller, method, HttpMethod.OPTIONS);
            }

            if (method.isAnnotationPresent(Before.class)) {
                Before before = method.getAnnotation(Before.class);
                filterClass(before.value(), controller, method, HttpMethod.BEFORE);
            }
            if (method.isAnnotationPresent(After.class)) {
                After before = method.getAnnotation(After.class);
                filterClass(before.value(), controller, method, HttpMethod.AFTER);
            }
        }
    }

    private void filterClass(String path, Object controller, Method method, HttpMethod httpMethod) {
        String domain = DEFAULT_DOMAIN;
        String acceptType = DEFAULT_ACCEPT_TYPE;
        if (method.isAnnotationPresent(Domain.class)) {
            domain = method.getAnnotation(Domain.class).value();
        }
        if (method.isAnnotationPresent(AcceptType.class)) {
            acceptType = method.getAnnotation(AcceptType.class).value();
        }
        filter(httpMethod, path, domain, acceptType, () -> method.invoke(controller));
    }

    private void methodClass(String path, Object controller, Method method, HttpMethod httpMethod) {

        String domain = DEFAULT_DOMAIN;
        String acceptType = DEFAULT_ACCEPT_TYPE;
        long cache = 0;
        boolean json = method.isAnnotationPresent(Json.class);
        TemplateEngine templateEngine = null;

        if (method.isAnnotationPresent(Domain.class)) {
            domain = method.getAnnotation(Domain.class).value();
        }
        if (method.isAnnotationPresent(AcceptType.class)) {
            acceptType = method.getAnnotation(AcceptType.class).value();
        }

        if (method.isAnnotationPresent(Cacheable.class)) {
            cache = method.getAnnotation(Cacheable.class).expire();
        }

        if (method.isAnnotationPresent(Template.class)) {
            Template.TemplateEngine t = method.getAnnotation(Template.class).value();
            templateEngine = templateMap.get(t);
            if (templateEngine == null) {
                logger.error("Can not find TemplateEngine for method: " + controller.getClass().getSimpleName() + "." + method.getName() + " in templateMap", new Throwable());
                return;
            }
        }

        if (json) {
            route(httpMethod, path, domain, () -> {
                Response.get().type("application/json");
                return method.invoke(controller);
            }, cache, acceptType, (ResponseTransformer) GSON::toJson);
            return;
        }

        if (templateEngine != null) {
            route(httpMethod, path, domain, () -> method.invoke(controller), cache, acceptType, templateEngine);
            return;
        }

        route(httpMethod, path, domain, () -> method.invoke(controller), cache, acceptType);
    }

    public RvcServer addTemplate(Template.TemplateEngine template, TemplateEngine engine) {
        templateMap.put(template, engine);
        return this;
    }

    public static void halt() {
        throw new HaltException();
    }

    public static void halt(int status) {
        throw new HaltException(status);
    }

    public static void halt(String body) {
        throw new HaltException(body);
    }

    public static void halt(int status, String body) {
        throw new HaltException(status, body);
    }
}
