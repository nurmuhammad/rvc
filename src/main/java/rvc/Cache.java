package rvc;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nurmuhammad on 09-Jan-16.
 */

public class Cache {

    static final String cacheFile = $.getCurrentClassPath() + File.separator + "cache.db";


    /*static DB db = DBMaker
            .newTempFileDB()
            .transactionDisable()
            .closeOnJvmShutdown()
            .cacheLRUEnable()
            .mmapFileEnableIfSupported()
            .mmapFileEnablePartial()
            .make();*/

    private static final DB db = DBMaker
            .fileDB(cacheFile)
            .fileMmapEnableIfSupported()
            .fileMmapPreclearDisable()
            .closeOnJvmShutdownWeakReference()
            .fileChannelEnable()
            .make();

    private static HTreeMap cacheMap = db.hashMap("map")
            .createOrOpen();
    private static HTreeMap cacheLifes= db.hashMap("expire")
            .createOrOpen();

    /*static HTreeMap<String, Object> cacheMap = db.getHashMap("CacheObjects");
    //    static HTreeMap<String, Long> cacheLifes = db.getHashMap("CacheObjectsLife");
    static HashMap<String, Long> cacheLifes = new HashMap<>();*/

    public synchronized static <T> T put(String key, T value) {
        cacheLifes.remove(key);
        if (value == null) {
            cacheMap.remove(key);
            return null;
        }
        cacheMap.put(key, value);
        return value;
    }

    public synchronized static <T> T put(String key, T value, long expire) {
        if (value == null) {
            cacheLifes.remove(key);
            cacheMap.remove(key);
            return null;
        }
        cacheLifes.put(key, (System.currentTimeMillis() + expire));
        cacheMap.put(key, value);
        return value;
    }

    public static <T> T get(String key) {
        if (key == null) return null;

        Long last = (Long) cacheLifes.get(key);

        if (last == null) {
            return (T) cacheMap.get(key);
        }

        if (last > System.currentTimeMillis()) {
            return (T) cacheMap.get(key);
        }

        cacheMap.remove(key);
        cacheLifes.remove(key);
        return null;
    }

    public static <T> T get(String key, long expire, Result<T> result) {
        T r = get(key);
        if (r == null) {
            r = Cache.put(key, result.execute(), expire);
        }
        return r;
    }

    public interface Result<T> {
        T execute();
    }
}
