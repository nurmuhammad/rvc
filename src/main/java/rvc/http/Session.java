package rvc.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.*;

public class Session {

    private static final Logger logger = LoggerFactory.getLogger(Session.class);

    public static Session get(){
        return Request.get().session();
    }

    public static Session get(boolean create){
        return Request.get().session(create);
    }

    final HttpSession session;

    public Session(HttpSession session) {
        this.session = session;
    }

    public HttpSession raw() {
        return session;
    }

    public <T> T attribute(String name) {
        return (T) session.getAttribute(name);
    }

    public void attribute(String name, Object value) {
        session.setAttribute(name, value);
    }

    public Collection<String> attributes() {

        synchronized (session) {
            Enumeration<String> attributes = session.getAttributeNames();
            ArrayList<String> names = new ArrayList<>();
            while (attributes.hasMoreElements())
                names.add(attributes.nextElement());
            return names;
        }
    }

    public String id() {
        return session.getId();
    }

    public void invalidate() {
        session.invalidate();
    }

    public void remove(String name) {
        session.removeAttribute(name);
    }
}