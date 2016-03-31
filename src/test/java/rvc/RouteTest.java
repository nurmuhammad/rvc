package rvc;


import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RouteTest {

    @Test
    public void route(){
        Route route = new Route();
        route.path = "/test/*/route";

        assertTrue(route.matchPath("/test/my/route"));
        assertFalse(route.matchPath("/1/test/my/route"));
    }

    @Test
    public void domain(){
        Route route = new Route();
        route.domain = "*.rvcjava.com";

        assertTrue(route.matchDomain("rvcjava.com"));
        assertTrue(route.matchDomain("sub.rvcjava.com"));
        assertFalse(route.matchDomain("a.rvcjava.net"));

    }

    @Test
    public void parser(){
        String[] strings = Route.parsePath("/test/my/route/");

        assertArrayEquals(strings, new String[]{"test", "my", "route"});

    }

}
