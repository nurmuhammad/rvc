package rvc.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rvc.Context;
import rvc.Route;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

public class Request {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    public static Request get() {
        return Context.get(Request.class);
    }

    HttpServletRequest servletRequest;

    Session session = null;

    private boolean forwarded = false;

    private Set<String> headers = null;

    private Map<String, String> params;

    private List<String> splats;

    Route route;

    public Request(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public void setRoute(Route route) {
        this.route = route;
        params = null;
        splats = null;
    }

    public HttpServletRequest raw() {
        return servletRequest;
    }

    public Session session() {
        if (session == null) {
            session = new Session(servletRequest.getSession());
        }
        return session;
    }

    public Session session(boolean create) {
        if (session == null) {
            HttpSession httpSession = servletRequest.getSession(create);
            if (httpSession != null) {
                session = new Session(httpSession);
            }
        }
        return session;
    }

    public void forward(String path) throws ServletException, IOException {
        RequestDispatcher dispatcher = servletRequest.getRequestDispatcher(path);
        forwarded = true;
        dispatcher.forward(servletRequest, Response.get().response);
    }

    public boolean isForwarded() {
        return forwarded;
    }

    public Map<String, String> params() {
        if (params == null) {
            params = route.getParams(servletRequest.getRequestURI());
        }
        return params;
    }

    public String params(String param) {
        if (param == null) {
            return null;
        }

        if (param.startsWith(":")) {
            return params().get(param.toLowerCase());
        } else {
            return params().get(":" + param.toLowerCase());
        }
    }

    public List<String> splats() {
        if (splats == null) {
            splats = route.getSplats(servletRequest.getRequestURI());
        }
        return splats;
    }

    public long contentLength() {
        return servletRequest.getContentLengthLong();
    }

    public String header(String header) {
        return servletRequest.getHeader(header);
    }

    public String requestMethod() {
        return servletRequest.getMethod();
    }

    public String scheme() {
        return servletRequest.getScheme();
    }

    public String host() {
        return servletRequest.getHeader("host");
    }

    public String userAgent() {
        return servletRequest.getHeader("user-agent");
    }

    public int port() {
        return servletRequest.getServerPort();
    }

    public String pathInfo() {
        return servletRequest.getPathInfo();
    }

    public String servletPath() {
        return servletRequest.getServletPath();
    }

    public String contextPath() {
        return servletRequest.getContextPath();
    }

    public String url() {
        return servletRequest.getRequestURL().toString();
    }

    public String contentType() {
        return servletRequest.getContentType();
    }

    public String ip() {
        return servletRequest.getRemoteAddr();
    }

    public String queryString() {
        return servletRequest.getQueryString();
    }

    public void attribute(String attribute, Object value) {
        servletRequest.setAttribute(attribute, value);
    }

    public <T> T attribute(String attribute) {
        return (T) servletRequest.getAttribute(attribute);
    }

    public Set<String> attributes() {
        Set<String> attrList = new HashSet<>();
        Enumeration<String> attributes = servletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    public String queryParams(String queryParam) {
        return this.servletRequest.getParameter(queryParam);
    }

    public String[] queryParamsValues(String queryParam) {
        return this.servletRequest.getParameterValues(queryParam);
    }

    public String url2() {
        StringBuilder builder = new StringBuilder(servletRequest.getScheme())
                .append("://")
                .append(servletRequest.getHeader("host"));

        if (servletRequest.getRequestURI() != null) {
            builder.append(servletRequest.getRequestURI());
        }
        if (servletRequest.getQueryString() != null) {
            builder.append("?")
                    .append(servletRequest.getQueryString());
        }

        return builder.toString();
    }

    public String path() {
        StringBuilder builder = new StringBuilder();

        if (servletRequest.getRequestURI() != null) {
            builder.append(servletRequest.getRequestURI());
        } else {
            builder.append("/");
        }

        if (servletRequest.getQueryString() != null) {
            builder.append("?")
                    .append(servletRequest.getQueryString());
        }

        return builder.toString();
    }

}