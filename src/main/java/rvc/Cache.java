package rvc;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.util.HashMap;

/**
 * Created by Nurmuhammad on 09-Jan-16.
 */

public class Cache {

    static DB db = DBMaker
            .newTempFileDB()
            .transactionDisable()
            .closeOnJvmShutdown()
            .cacheLRUEnable()
            .mmapFileEnableIfSupported()
            .mmapFileEnablePartial()
            .make();

    static HTreeMap<String, Object> cacheMap = db.getHashMap("CacheObjects");
    //    static HTreeMap<String, Long> cacheLifes = db.getHashMap("CacheObjectsLife");
    static HashMap<String, Long> cacheLifes = new HashMap<>();

    public synchronized static Object put(String key, Object value) {
        cacheLifes.remove(key);
        if (value == null) {
            cacheMap.remove(key);
            return null;
        }
        cacheMap.put(key, value);
        return value;
    }

    public synchronized static Object put(String key, Object value, long expire) {
        if (value == null) {
            cacheLifes.remove(key);
            cacheMap.remove(key);
            return null;
        }
        cacheLifes.put(key, (System.currentTimeMillis() + expire));
        cacheMap.put(key, value);
        return value;
    }

    public static Object get(String key) {
        if (key == null) return null;

        Long last = cacheLifes.get(key);

        if (last == null) {
            return cacheMap.get(key);
        }

        if (last > System.currentTimeMillis()) {
            return cacheMap.get(key);
        }

        cacheMap.remove(key);
        cacheLifes.remove(key);
        return null;
    }

}
