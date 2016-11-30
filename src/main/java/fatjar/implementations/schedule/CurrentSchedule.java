package fatjar.implementations.schedule;

import fatjar.Log;
import fatjar.Schedule;

import java.util.HashMap;
import java.util.Map;

public class CurrentSchedule {


    private static final Map<String, Schedule> scheduleMap = new HashMap<>();

    private CurrentSchedule() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("never call constructor, use create method");
    }

    @SuppressWarnings("unchecked")
    public static Schedule create(Schedule.Type type) {
        if (!scheduleMap.containsKey(type.name())) {
            String packageName = CurrentSchedule.class.getPackage().getName();
            try {
                Class<? extends Schedule> scheduleClass = Class.forName(packageName + "." + type.name()).asSubclass(Schedule.class);
                Schedule instance = scheduleClass.newInstance();
                scheduleMap.put(type.name(), instance);
            } catch (Exception e) {
                Log.error("could not create schedule of type: " + type + " error: " + e, e);
            }
        }
        return scheduleMap.get(type.name());
    }

}
