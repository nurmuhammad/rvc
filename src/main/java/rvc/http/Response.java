package rvc.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rvc.Context;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Response {

    private static final Logger logger = LoggerFactory.getLogger(Response.class);

    HttpServletResponse response;
    Object content;
    boolean gzip = false;
    boolean redirected = false;

    public static Response get() {
        return Context.get(Response.class);
    }

    public Response(HttpServletResponse response) {
        this.response = response;
    }

    public Object content() {
        return content;
    }

    public void content(Object content) {
        this.content = content;
    }

    public void status(int code) {
        response.setStatus(code);
    }

    public void type(String contentType) {
        response.setContentType(contentType);
    }

    public HttpServletResponse raw() {
        return response;
    }

    public void redirect(String location) {
        try {
            response.sendRedirect(location);
            redirected = true;
        } catch (IOException e) {
            logger.error("Redirect fail", e);
        }
    }

    public void redirect(String path, int statusCode) {
        response.setStatus(statusCode);
        response.setHeader("Location", path);
        response.setHeader("Connection", "close");
        try {
            response.sendError(statusCode);
            redirected = true;
        } catch (IOException e) {
            logger.error("Redirect fail", e);
        }
    }

    public void header(String header, String value) {
        response.addHeader(header, value);
    }

    public void gzip(){
        this.gzip = true;
    }

    public void gzip(boolean gzip){
        this.gzip = gzip;
    }

    public boolean isGzip(){
        return this.gzip;
    }

    public boolean isRedirected(){
        return redirected;
    }
}