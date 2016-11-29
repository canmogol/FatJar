package fatjar;

import fatjar.implementations.metrics.CurrentMetrics;

import java.util.Map;

public interface Metrics {

    static Metrics create() {
	return CurrentMetrics.getInstance();
    }

    Metrics add(String key, Object value);

    Map<String, Object> getMetrics();

    enum Key {
	ServerType, ServerPort, ServerHostname, ServerServices, ServerCreated, ServerStarted, ServerStartFailed, LastRequestTime, LastErrorTime;
    }
}
