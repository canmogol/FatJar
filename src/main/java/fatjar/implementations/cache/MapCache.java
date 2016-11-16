package fatjar.implementations.cache;

import fatjar.Cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MapCache<K, V> implements Cache<K, V> {

    private final String name;
    private static Map<String, Map> map = new ConcurrentHashMap<>();

    public MapCache(String name) {
        this.name = name;
        map.putIfAbsent(name, new HashMap<K, V>());
    }

    @SuppressWarnings("unchecked")
    private Map<K, V> cache() {
        return map.get(this.name);
    }

    @Override
    public Type getType() {
        return Type.Map;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public V get(K key) {
        return cache().get(key);
    }

    @Override
    public Map<K, V> getAll() {
        Map<K, V> map = new HashMap<>();
        cache().keySet()
                .stream()
                .forEach(key -> {
                    map.put(key, cache().get(key));
                });
        return map;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> map = new HashMap<>();
        cache().keySet()
                .stream()
                .filter(keys::contains)
                .forEach(key -> {
                    map.put(key, cache().get(key));
                });
        return map;
    }

    @Override
    public boolean containsKey(K key) {
        return cache().containsKey(key);
    }

    @Override
    public void put(K key, V value) {
        cache().put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        cache().putAll(map);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return cache().putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        return cache().remove(key);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return cache().replace(key, oldValue, newValue);
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        cache().keySet()
                .stream()
                .filter(keys::contains)
                .forEach(key -> {
                    cache().remove(key);
                });
    }

    @Override
    public void clear() {
        cache().clear();
    }


}
