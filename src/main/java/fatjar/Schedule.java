package fatjar;

import fatjar.implementations.schedule.CurrentSchedule;

import java.util.concurrent.TimeUnit;

public interface Schedule {

    static Schedule create() {
        return create(Type.ThreadSchedule);
    }

    static Schedule create(Type type) {
        return CurrentSchedule.create(type);
    }

    void add(Scheduled scheduled);

    void add(Scheduled scheduled, long period, TimeUnit unit);

    enum Type {
        ThreadSchedule, QuartzSchedule;
    }

    @FunctionalInterface
    interface Scheduled {
        void apply();
    }
}
