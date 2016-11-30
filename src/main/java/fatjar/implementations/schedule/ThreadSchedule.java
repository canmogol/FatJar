package fatjar.implementations.schedule;

import fatjar.Schedule;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class ThreadSchedule implements Schedule {

    private ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(5);

    @Override
    public void add(Scheduled scheduled) {
        final Runnable runnable = scheduled::apply;
        final ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(runnable, 0, 1, SECONDS);
        scheduler.schedule((Runnable) () -> future.cancel(true), 0, SECONDS);
    }

    @Override
    public void add(Scheduled scheduled, long period, TimeUnit unit) {
        final Runnable runnable = scheduled::apply;
        final ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(runnable, 0, period, unit);
        scheduler.schedule((Runnable) () -> future.cancel(true), 0, SECONDS);
    }

}
