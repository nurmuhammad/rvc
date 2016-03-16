package rvc.http;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rvc.Context;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class Request {

    private static final Logger logger = LoggerFactory.getLogger(Request.class);

    public static Request get() {
        return Context.get(Request.class);
    }

    HttpServletRequest servletRequest;

    private Session session = null;

    private boolean forwarded = false;

    public Request(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
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


    public String url() {
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