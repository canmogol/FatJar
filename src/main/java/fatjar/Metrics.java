package fatjar;

import fatjar.implementations.metrics.CurrentMetrics;

import java.util.*;
import java.util.Date;

public interface Metrics {

    static Metrics create() {
	return CurrentMetrics.getInstance();
    }

    Metrics add(String key, Object value);

    Map<String, Object> getMetrics();

    void addRequestTime(Date date);

    enum Key {
	ServerType, ServerPort, ServerHostname, ServerServices, ServerCreated, ServerStarted, ServerStartFailed, LastRequestTimes, LastErrorTime;
    }
}
