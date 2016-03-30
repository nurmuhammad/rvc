package rvc;

import java.util.*;

public class Route {

    HttpMethod httpMethod;
    String path;
    String domain;
    String acceptedType;
    Action action;
    Filter filter;
    long cacheExpire = 0;
    Class<? extends Exception> exception;

    private String[] pathParts = null;

    boolean match(HttpMethod httpMethod, String path) {
        if (this.httpMethod != httpMethod) {
            return false;
        }

        if (this.path.equals(RvcServer.ALL_PATH))
            return true;

        return matchPath(path);
    }

    boolean matchPath(String path) {
        if (this.path.equals(path)) {
            return true;
        }

        if (pathParts == null) {
            pathParts = parsePath(this.path);
        }
        String[] pathParts2 = parsePath(path);

        if (pathParts.length == pathParts2.length) {
            for (int i = 0; i < pathParts.length; i++) {
                String part = pathParts[i];
                String part2 = pathParts2[i];
                if (!part.equals(part2) && !part.equals("*") && !part.startsWith(":")) {
                    return false;
                }
            }
            return true;
        } else {
            if (!this.path.endsWith("*"))
                return false;
            if (pathParts.length > pathParts2.length)
                return false;

            for (int i = 0; i < pathParts.length; i++) {
                String part = pathParts[i];
                String part2 = pathParts2[i];
                if (!part.equals(part2) && !part.startsWith(":") && !part.equals("*")) {
                    return false;
                }
            }
            return true;
        }

    }

    boolean matchDomain(String domain) {
        return matchDomain(this.domain, domain);
    }

    boolean matchAcceptedType(String acceptedType) {
        if (RvcServer.DEFAULT_ACCEPT_TYPE.equals(this.acceptedType)) return true;

        if (this.acceptedType.contains(acceptedType)) return true;

        return false;
    }

    static String[] parsePath(String path) {
        ArrayList<String> result = new ArrayList<>();
        String parts[] = path.split("/");
        for (String part : parts) {
            if (part.length() != 0)
                result.add(part);
        }
        return result.toArray(new String[result.size()]);
    }

    public Map<String, String> getParams(String path) {
        Map<String, String> params = new HashMap<>();

        if (pathParts == null) {
            pathParts = parsePath(this.path);
        }
        String[] pathParts2 = parsePath(path);


        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].startsWith(":")) {
                params.put(pathParts[i], pathParts2[i]);
            }
        }
        return Collections.unmodifiableMap(params);
    }

    public List<String> getSplats(String path) {
        List<String> splats = new ArrayList<>();

        if (pathParts == null) {
            pathParts = parsePath(this.path);
        }
        String[] pathParts2 = parsePath(path);

        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].equals("*")) {

                StringBuilder splat = new StringBuilder(pathParts2[i]);
                if (pathParts.length != pathParts2.length && i == pathParts.length - 1) {
                    for (int j = i + 1; j < pathParts2.length; j++) {
                        splat.append("/");
                        splat.append(pathParts2[j]);
                    }
                }

                splats.add(splat.toString());
            }
        }

        return Collections.unmodifiableList(splats);
    }

    public static boolean matchDomain(String domain, String serverName) {
        if (domain == null || serverName == null) return false;
        if (RvcServer.DEFAULT_DOMAIN.equals(domain) || domain.equals(serverName)) return true;
        String[] domainPart = domain.split("\\.");
        String[] serverNamePart = serverName.split("\\.");

        if (domainPart.length == serverNamePart.length) {
            for (int i = 0; i < domainPart.length; i++) {

                if (!domainPart[i].equals(serverNamePart[i]) && !domainPart[i].equals("*")) {
                    return false;
                }
            }
            return true;
        } else {
            if (domain.startsWith("*")) {
                $.reverse(domainPart);
                $.reverse(serverNamePart);
                for (int i = 0; i < domainPart.length; i++) {
                    if (!domainPart[i].equals(serverNamePart[i]) && !domainPart[i].equals("*")) {
                        return false;
                    }

                    if (serverNamePart.length - 1 == i) {
                        return true;
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isParam(String pathPart) {
        return pathPart.startsWith(":");
    }

    public static boolean isSplat(String pathPart) {
        return pathPart.equals("*");
    }

    public static void main(String[] args) throws InterruptedException {

        Route route = new Route();
        route.path = "/:name/*";
        route.httpMethod = HttpMethod.BEFORE;

        boolean t = route.match(HttpMethod.BEFORE, "url/text/1");
        System.out.println(t);

    }
}
