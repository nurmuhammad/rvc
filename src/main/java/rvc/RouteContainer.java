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
        for (Route route : routes) {
            if (route.httpMethod != httpMethod) {
                continue;
            }

            if (route.matchDomain(domain) && route.match(httpMethod, path)) {
                if (route.matchAcceptedType(acceptedType)) {
                    return route;
                }
            }
        }
        return null;

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