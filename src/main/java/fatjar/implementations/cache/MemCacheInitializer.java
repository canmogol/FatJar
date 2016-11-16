package fatjar.implementations.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.whalin.MemCached.SockIOPool;

public class MemCacheInitializer {
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
