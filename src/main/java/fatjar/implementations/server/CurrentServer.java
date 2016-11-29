package fatjar.implementations.server;

import fatjar.Date;
import fatjar.Log;
import fatjar.Metrics;
import fatjar.Server;

import java.util.Map;

public class CurrentServer {

    private CurrentServer() throws UnsupportedOperationException {
	throw new UnsupportedOperationException("never call constructor, use create method");
    }

    public static Server create(Server.Type type, Map<Server.ServerParams, String> params) {
	String packageName = CurrentServer.class.getPackage().getName();
	Server server = null;
	try {
	    Class<? extends Server> serverClass = Class.forName(packageName + "." + type.name()).asSubclass(Server.class);
	    server = serverClass.getConstructor(Map.class).newInstance(params);
	    Metrics.create().add(Metrics.Key.ServerCreated.name(), Date.create().getDate());
	} catch (Exception e) {
	    Log.error("could not create server of type: " + type + " error: " + e, e);
	}
	return server;
    }

}
