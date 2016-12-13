package rvc;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nurmuhammad
 */

public class RvcFileHandler extends ResourceHandler {
    GzipHandler gzipHandler = new GzipHandler();

    public RvcFileHandler() {
        super();
        gzipHandler.setHandler(this);
    }

    public void handleGzip(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        gzipHandler.handle(target, baseRequest, request, response);
    }
}
