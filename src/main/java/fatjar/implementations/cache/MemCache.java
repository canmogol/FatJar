package fatjar.implementations.cache;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import fatjar.Cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
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

	public static class MemCacheInitializer {
                private static final String PROP_NAME = "memcache.properties";

                public void initializeSockIOPool(String name) {

                        Properties memcacheProp = new Properties();
                        InputStream input = null;
                        SockIOPool pool = SockIOPool.getInstance(name);
                        input = this.getClass().getClassLoader().getResourceAsStream(PROP_NAME);
                        try {
                                memcacheProp.load(input);
                                input.close();
                                pool.setServers(memcacheProp.getProperty("serversList").split(","));
                                pool.setFailover(Boolean.parseBoolean(memcacheProp.getProperty("failover")));
                                pool.setInitConn(Integer.parseInt(memcacheProp.getProperty("initConn")));
                                pool.setMinConn(Integer.parseInt(memcacheProp.getProperty("minConn")));
                                pool.setMaxConn(Integer.parseInt(memcacheProp.getProperty("maxConn")));
                                pool.setMaintSleep(Long.parseLong(memcacheProp.getProperty("maintSleep")));
                                pool.setNagle(Boolean.parseBoolean(memcacheProp.getProperty("nagle")));
                                pool.setSocketTO(Integer.parseInt(memcacheProp.getProperty("docketTO")));
                                pool.setAliveCheck(Boolean.parseBoolean(memcacheProp.getProperty("aliveCheck")));
                        } catch (IOException e) {
                                e.printStackTrace();
                                String[] servers = { "localhost:11211" };
                                pool.setServers(servers);
                                pool.setFailover(true);
                                pool.setInitConn(10);
                                pool.setMinConn(5);
                                pool.setMaxConn(250);
                                pool.setMaintSleep(30);
                                pool.setNagle(false);
                                pool.setSocketTO(3000);
                                pool.setAliveCheck(true);
                        }

                        pool.initialize();
                }
        }
}
