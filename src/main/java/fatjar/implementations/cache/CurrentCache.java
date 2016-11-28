package fatjar.implementations.cache;

import fatjar.Cache;
import fatjar.Log;

import java.util.HashMap;
import java.util.Map;

public class CurrentCache {

    private static final Map<String, Cache> cacheMap = new HashMap<>();

    private CurrentCache() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Cache<String, Object> create(Cache.Type type, String name) {
        String key = type.name() + "/" + name;
        if (!cacheMap.containsKey(key)) {
            String packageName = CurrentCache.class.getPackage().getName();
            try {
                Class<? extends Cache> cacheClass = Class.forName(packageName + "." + type.name()).asSubclass(Cache.class);
                Cache instance = cacheClass.getConstructor(String.class).newInstance(name);
                cacheMap.put(key, instance);
            } catch (Exception e) {
                Log.error("could not create cache of type: " + type + " for name: " + name + " error: " + e, e);
            }
        }
        return cacheMap.get(key);
    }

}
