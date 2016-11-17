package fatjar.implementations.cache;

import com.whalin.MemCached.MemCachedClient;
import fatjar.Cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MemCache<K, V> implements Cache<K, V> {

	private final String name;

	private static Map<String, MemCachedClient> map = new ConcurrentHashMap<>();

	public MemCache(String name) {
		this.name = name;
		this.initialize();

	}

	public void initialize() {

		new MemCacheInitializer().initializeSockIOPool(name);
		MemCachedClient mcc = new MemCachedClient(name);
		map.put(name, mcc);

	}
	
		
	private MemCachedClient cache() {
		return map.get(this.name);
	}

	@Override
	public Type getType() {
		return Type.MapCache;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public V get(K key) {
		return (V) cache().get(key.toString());
	}

	@Override
	public Map<K, V> getAll(Set<K> keys) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public boolean containsKey(K key) {
		return cache().keyExists(key.toString());
	}

	@Override
	public void put(K key, V value) {
		cache().add(key.toString(), value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		for (K key : map.keySet()) {
			cache().add(key.toString(), map.get(key));
		}
	}

	@Override
	public V putIfAbsent(K key, V value) {

		if (!cache().keyExists(key.toString())) {
			cache().add(key.toString(), value);
		}

		return (V) cache().get(key.toString());
	}

	@Override
	public V remove(K key) {
		V value = (V) cache().get(key.toString());
		cache().delete(key.toString());
		return value;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {

		return cache().replace(key.toString(), newValue);
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

}
