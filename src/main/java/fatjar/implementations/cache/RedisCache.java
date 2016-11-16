package fatjar.implementations.cache;

import fatjar.Cache;

import java.util.Map;
import java.util.Set;

public class RedisCache<K, V> implements Cache<K, V> {
    private final String name;

    public RedisCache(String name) {
        this.name = name;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public V get(K key) {
        return null;
    }

    @Override
    public Map<K, V> getAll(Set<K> keys) {
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return false;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {

    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public V remove(K key) {
        return null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public void removeAll(Set<? extends K> keys) {

    }

    @Override
    public void clear() {

    }

}
