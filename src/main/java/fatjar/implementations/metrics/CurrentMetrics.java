package fatjar.implementations.metrics;

import fatjar.Metrics;

import java.util.Map;
import java.util.TreeMap;

public class CurrentMetrics implements Metrics {

    private Map<String, Object> metrics = new TreeMap<>();

    @Override
    public Metrics add(String key, Object value) {
	metrics.put(key, value);
	return this;
    }

    @Override
    public Map<String, Object> getMetrics() {
	return metrics;
    }

    public static Metrics getInstance() {
	return Instance.instance;
    }

    private static class Instance {

	private static final CurrentMetrics instance = new CurrentMetrics();

	private Instance() {
	}
    }

}
