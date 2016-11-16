package fatjar.implementations.cache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;

import fatjar.Cache;

public class MemCache<K, V> implements Cache<K, V> {

	private final String name;

	private static Map<String, MemCachedClient> map = new ConcurrentHashMap<>();

	public MemCache(String name) {
		this.name = name;
		this.initialize();

	}

	public void initialize() {

		String[] servers = { "localhost:11211" };
		SockIOPool pool = SockIOPool.getInstance(name);
		pool.setServers(servers);
		pool.setFailover(true);
		pool.setInitConn(10);
		pool.setMinConn(5);
		pool.setMaxConn(250);
		pool.setMaintSleep(30);
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setAliveCheck(true);
		pool.initialize();
		MemCachedClient mcc = new MemCachedClient(name);
		map.put(name, mcc);

	}
	
		
	private MemCachedClient cache() {
		return map.get(this.name);
	}

	@Override
	public Type getType() {
		return Type.Memcache;
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
