package fatjar;

import fatjar.implementations.cache.CurrentCache;

import java.util.Map;
import java.util.Set;

public interface Cache<K, V> {

    @SuppressWarnings("unchecked")
    static <K, V> Cache<K, V> create() {
        return (Cache<K, V>) Cache.create(Type.MapCache, Type.MapCache.name());
    }

    @SuppressWarnings("unchecked")
    static <K, V> Cache<K, V> create(String name) {
        return (Cache<K, V>) Cache.create(Type.MapCache, name);
    }

    @SuppressWarnings("unchecked")
    static <K, V> Cache<K, V> create(Type type, String name) {
        return (Cache<K, V>) CurrentCache.create(type, name);
    }

    Cache.Type getType();

    String getName();

    V get(K key);

    boolean containsKey(K key);

    void put(K key, V value);

    void putAll(java.util.Map<? extends K, ? extends V> map);

    V putIfAbsent(K key, V value);

    V remove(K key);

    boolean replace(K key, V oldValue, V newValue);
    enum Type {
        MapCache, MemCache, RedisCache
    }

}
