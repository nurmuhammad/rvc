package rvc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author nurmuhammad
 */

public class Context {
    private static final ThreadLocal<Map> threadLocalScope = new ThreadLocal<>();

    public static <T> T get(Class<T> type) {
        Map<Class<T>, T> map = threadLocalScope.get();
        if (map == null) return null;
        return map.get(type);
    }

    public static <T> void set(Class<T> type, T instance) {
        Map<Class<T>, T> map = threadLocalScope.get();
        if (map == null) {
            map = new HashMap<>();
            threadLocalScope.set(map);
        }
        map.put(type, instance);
    }

}