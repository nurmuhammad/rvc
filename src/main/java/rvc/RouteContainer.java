package rvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class RouteContainer {

    private static final Logger LOG = LoggerFactory.getLogger(RouteContainer.class);

    List<Route> routes = new ArrayList<>();
    List<Route> filters = new ArrayList<>();
    List<Route> exceptions = new ArrayList<>();

    public void clear() {
        routes.clear();
        filters.clear();
        exceptions.clear();
    }

    public void addRoute(HttpMethod method, String path, String domain, String acceptedType, long cacheExpire, Action action) {

        Route route = new Route();
        route.httpMethod = method;
        route.path = path;
        route.domain = domain;
        route.acceptedType = acceptedType;
        route.action = action;
        route.cacheExpire = cacheExpire;

        routes.add(route);
        LOG.debug("Route added:" + route);
    }

    public void addFilter(HttpMethod method, String path, String domain, String acceptedType, Filter filter) {

        Route route = new Route();
        route.httpMethod = method;
        route.path = path;
        route.domain = domain;
        route.acceptedType = acceptedType;
        route.filter = filter;

        filters.add(route);
        LOG.debug("Filter added:" + route);
    }

    public void addException(Class<? extends Exception> exception, String domain, Action action) {
        Route route = new Route();
        route.domain = domain;
        route.action = action;
        route.exception = exception;
        exceptions.add(route);
        LOG.debug("Exception handler added:" + route);
    }

    public Route findMatchRoute(HttpMethod httpMethod, String path, String domain, String acceptedType) {
        ArrayList<Route> matchedRoutes = new ArrayList<>();
        for (Route route : routes) {
            if (route.httpMethod != httpMethod) {
                continue;
            }

            if (route.matchDomain(domain) && route.match(httpMethod, path)) {
                if (route.matchAcceptedType(acceptedType)) {
                    matchedRoutes.add(route);
                }
            }
        }

        if (matchedRoutes.size() == 0) return null;
        if (matchedRoutes.size() == 1) return matchedRoutes.get(0);

        for (Route route : matchedRoutes) {
            if (domain.equals(route.domain)) {
                return route;
            }
        }

        for (Route route : matchedRoutes) {
            if (!RvcServer.DEFAULT_DOMAIN.equals(route.domain)) {
                return route;
            }
        }

        Route moreSimilar = matchedRoutes.get(0);
        double distance0 = 0;
        for (Route route : matchedRoutes) {
            if (route == moreSimilar) continue;
            double distance = Similarity.similarity(path.replace("//", "/"), route.path.replace("//", "/"));
            if (distance == 1.0) return route;
            if (distance > distance0) {
                moreSimilar = route;
            }
        }
        if (moreSimilar != null) return moreSimilar;

        return matchedRoutes.get(0);
    }

    public List<Route> findMatchFilters(HttpMethod httpMethod, String path, String domain, String acceptedType) {
        List<Route> matchFilters = new ArrayList<>();

        for (Route route : filters) {
            if (route.httpMethod != httpMethod) {
                continue;
            }

            if (route.matchDomain(domain) && route.match(httpMethod, path)) {
                if (route.matchAcceptedType(acceptedType)) {
                    matchFilters.add(route);
                }
            }
        }
        return matchFilters;
    }

    public Route findMatchException(Class<? extends Exception> exception, String domain) {
        for (Route route : exceptions) {

            if (exception != null && route.matchDomain(domain)) {
                if (exception.equals(route.exception)) {
                    return route;
                }

                if (route.exception.isAssignableFrom(exception))
                    return route;

            }
        }
        return null;

    }

}