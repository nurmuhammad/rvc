package rvc;

import crypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class $ {

    static BCryptPasswordEncoder crypt = new BCryptPasswordEncoder();

    public static String encode(String password) {
        return crypt.encode(password);
    }

    public static boolean matches(CharSequence rawPassword, String encodedPassword) {
        return crypt.matches(rawPassword, encodedPassword);
    }

    public static int timestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static Date date(int timestamp) {
        return new Date(timestamp * 1000);
    }


    public static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof String)
            return ((String) value).trim().length() == 0;
        if (value instanceof Collection)
            return ((Collection) value).isEmpty();
        if (value instanceof Map)
            return ((Map) value).isEmpty();
        if (value instanceof Object[]) {
//        if(value.getClass().isArray()){
            return ((Object[]) value).length == 0;
        }
        return false;
    }

    public static String b64encode(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String b64decode(String s) {
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    public static String xor(String input, String KEY) {
        char[] key = KEY.toCharArray();
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            ret.append((char) (input.charAt(i) ^ key[i % key.length]));
        }
        return ret.toString();
    }

    public static void reverse(final Object[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        Object tmp;
        for (int i = 0; i < array.length / 2; i++) {
            tmp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = tmp;
        }
    }

    public static <T> T get(Class<T> type) {
        return Context.get(type);
    }
}
