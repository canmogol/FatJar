package fatjar.implementations.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import fatjar.Cache;
import redis.clients.jedis.Jedis;

public class RedisCache<K, V> implements Cache<K, V> {
	private final String name;

	private static Map<String, Jedis> map = new ConcurrentHashMap<>();

	public RedisCache(String name) {
		this.name = name;
	}

	public void initialize() {

		RedisCacheInitializer cacheInitializer = new RedisCacheInitializer();
		cacheInitializer.initializePool();
		Jedis jedis = new Jedis(cacheInitializer.getServerHost(), cacheInitializer.getServerPort());

		map.put(name, jedis);

	}

	private Jedis cache() {
		return map.get(this.name);
	}

	@Override
	public Type getType() {
		return Type.RedisCache;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public V get(K key) {
		return convertFromBytes(cache().get(convertToBytes(key)));
	}

	@Override
	public Map<K, V> getAll(Set<K> keys) {
		
		Map<K, V> map = new HashMap<>();
		Set<byte[]> arrrays = cache().keys("*".getBytes());
		
		for (byte[] bytes : arrrays) {
			 K key  = (K) convertFromBytes(bytes);
			 V value = convertFromBytes(cache().get(bytes));
			 map.put(key, value);
			 
		
		}
		return map;
	}

	@Override
	public boolean containsKey(K key) {
		
		return cache().keys("*".getBytes()).contains(convertToBytes(key));
	}

	@Override
	public void put(K key, V value) {
		cache().set(convertToBytes(key), convertToBytes(value));
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {

		for(K k : map.keySet()){
			cache().set(convertToBytes(k), convertToBytes(map.get(k)));	
		}
		
		
	}

	@Override
	public V putIfAbsent(K key, V value) {
		byte[] keyBytes = convertToBytes(key);
		cache().setnx(keyBytes, convertToBytes(value));		
		return convertFromBytes(cache().get(keyBytes));
	}

	@Override
	public V remove(K key) {
		byte[] keyBytes = convertToBytes(key);
		V value =convertFromBytes(cache().get(keyBytes));
		cache().del(keyBytes);
		return value;
	}

	@Override
	public boolean replace(K key, V oldValue, V newValue) {
		return false;
	}

	@Override
	public void removeAll(Set<? extends K> keys) {
		for(K key : keys){
			cache().del(convertToBytes(key));
		}
		
	}

	@Override
	public void clear() {
		cache().flushAll();
	}

	private <T> byte[] convertToBytes(T type) {
		byte[] bytes = null;
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
			out.writeObject(type);
			bytes = bos.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return bytes;
	}

	private V convertFromBytes(byte[] bytes) {
		V local = null;
		if(bytes == null){
			return null;
		}
		try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInput in = new ObjectInputStream(bis)) {
			local = (V) in.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return local;
	}
	
	
	public  static class RedisCacheInitializer {
		private static final String PROP_NAME = "rediscache.properties";

		public   String serverHost;
		public  int serverPort;

		public void initializePool() {

			Properties redisProp = new Properties();
			InputStream input = null;

			input = this.getClass().getClassLoader().getResourceAsStream(PROP_NAME);

			try {
				redisProp.load(input);
				input.close();
				serverHost = redisProp.getProperty("host", "localhost");
				String port = redisProp.getProperty("port", "6379");
				serverPort = new Integer(port).intValue();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

		public String getServerHost() {
			return serverHost;
		}

		public void setServerHost(String serverHost) {
			this.serverHost = serverHost;
		}

		public int getServerPort() {
			return serverPort;
		}

		public void setServerPort(int serverPort) {
			this.serverPort = serverPort;
		}
		
		
		
	}

}
