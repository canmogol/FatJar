package fatjar.implementations.metrics;

import fatjar.Metrics;

import java.util.*;

public class CurrentMetrics implements Metrics {

    private Map<String, Object> metrics = new TreeMap<>();
    private List<Date> requestDates = new LinkedList<>();

    private CurrentMetrics() {
    }

    public static Metrics getInstance() {
        Instance.instance.metrics.put(Metrics.Key.LastRequestTimes.name(), Instance.instance.requestDates);
        return Instance.instance;
    }

    @Override
    public Metrics add(String key, Object value) {
        metrics.put(key, value);
        return this;
    }

    @Override
    public Map<String, Object> getMetrics() {
        return metrics;
    }

    @Override
    public void addRequestTime(Date date) {
        if (requestDates.size() > 20) {
            requestDates.remove(0);
        }
        requestDates.add(date);
    }

    private static class Instance {

        private static final CurrentMetrics instance = new CurrentMetrics();

        private Instance() {
        }
    }

}
