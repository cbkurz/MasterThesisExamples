package kurz.ma.loadtest;

import kurz.ma.examples.simple.Main;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Driver {

    private static int processesStarted = 0;

    public static void main(String[] args) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        for (int i = 0; i < 5; i++) { // Warmup
            Main.main(new String[0]);
        }
        scheduledExecutorService.scheduleAtFixedRate(Driver::exec, 0, 1500, TimeUnit.MILLISECONDS);
        scheduledExecutorService.schedule(scheduledExecutorService::shutdown, 10, TimeUnit.SECONDS);
    }

    private static void exec() {
        System.out.println("Processes start: " + ++processesStarted);
        Main.main(new String[0]);
    }

    public static void busyWait(final int waitTimeMillies) {
        final Instant start = Instant.now();
        final Instant targetTime = start.plus(waitTimeMillies, ChronoUnit.MILLIS);
        System.out.println("Started at: " + start);
        Instant end;
        while (true) {
            end = Instant.now();
            if (end.isAfter(targetTime)) break;
        }
        System.out.println("Finnished at: " + end);
        System.out.println("Time spent: " + Duration.between(start, end));
    }
}
