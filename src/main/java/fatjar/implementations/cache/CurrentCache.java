package fatjar.implementations.cache;

import fatjar.Cache;

public class CurrentCache {

    private CurrentCache() throws Exception {
        throw new Exception("never call constructor, use create method");
    }

    public static Cache<String, Object> create(Cache.Type type, String name) {
        Cache<String, Object> cache;
        switch (type) {
            case Memcache:
                cache = new MemCache<>(name);
                break;
            case Redis:
                cache = new RedisCache<>(name);
                break;
            default:
                cache = new MapCache<>(name);
                break;
        }
        return cache;
    }

    public static Cache<String, Object> create(String name) {
        return create(Cache.Type.Map, name);
    }
}
