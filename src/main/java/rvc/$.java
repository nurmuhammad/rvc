package rvc;

import crypt.BCryptPasswordEncoder;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
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
        return new Date(timestamp * 1000L);
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

    public static String runFolder() {
        String runFolder;
        try {
            runFolder = URLDecoder.decode(ClassLoader.getSystemClassLoader().getResource(".").getPath(), "UTF-8");
            File tempFile = new File(runFolder);
            if (tempFile.isDirectory()) {
                runFolder = tempFile.getPath();
            } else runFolder = null;
        } catch (Throwable ignored) {
            runFolder = null;
        }

        try {
            if (runFolder == null) {
                File file = new java.io.File($.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
                if (file.exists() && file.isFile()) {
                    file = file.getParentFile();
                }
                runFolder = file.getPath();
                File tempFile = new File(runFolder);
                if (tempFile.isDirectory()) {
                    runFolder = tempFile.getPath();
                } else runFolder = null;
            }
        } catch (Throwable ignored) {
            runFolder = null;
        }

        return runFolder;
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

    // start reflection methods invokes
    public static Object invoke(Object data, String field) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method meth;
        Object obj = data;
        String[] flds = field.split("[.]");
        for (String fld : flds) {
            meth = obj.getClass().getMethod(methodGet(fld));
            obj = meth.invoke(obj);
        }
        return obj;
    }

    public static void setValue(Object data, String field, Object value)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        setValue(data, field, value, value.getClass());
    }

    public static void setValue(Object data, String field, Object value, Class clazz)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method meth;
        Object obj = data;
        String[] flds = field.split("[.]");
        for (int i = 0; i < flds.length - 1; i++) {
            meth = obj.getClass().getMethod(methodGet(flds[i]));
            obj = meth.invoke(obj);
        }
        meth = obj.getClass().getMethod(methodSet(flds[flds.length - 1]), clazz);
        meth.invoke(obj, value);
    }

    public static String methodGet(String field) {
        return "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }

    public static String methodSet(String field) {
        return "set" + field.substring(0, 1).toUpperCase() + field.substring(1);
    }
    // end reflection methods invokes


    public static boolean equal(boolean eq1, boolean eq2){
        return eq1 == eq2;
    }

    public static boolean equal(char eq1, char eq2){
        return eq1 == eq2;
    }

     public static boolean equal(long eq1, long eq2){
        return eq1 == eq2;
    }

    public static boolean equal(float eq1, float eq2){
        return Float.floatToIntBits(eq1) == Float.floatToIntBits(eq2);
    }

    public static boolean equal(double eq1, double eq2){
        return Double.doubleToLongBits(eq1) == Double.doubleToLongBits(eq2);
    }

    public static boolean equal(Object eq1, Object eq2){
        return eq1 == null ? eq2 == null : eq1.equals(eq2);
    }
}
