package rvc;

import javax.servlet.http.HttpServletResponse;

public class HaltException extends RuntimeException {

    private int statusCode = HttpServletResponse.SC_OK;
    private Object body;
    private String acceptedType;

    public HaltException() {
    }

    public HaltException(int statusCode) {
        this.statusCode = statusCode;
    }

    public HaltException(Object body) {
        this.body = body;
    }

    public HaltException(int statusCode, Object body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public HaltException(int statusCode, Object body, String acceptedType) {
        this.statusCode = statusCode;
        this.body = body;
        this.acceptedType = acceptedType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getBody() {
        return body;
    }

    public String getAcceptedType() {
        return acceptedType;
    }
}
