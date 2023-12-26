package kurz.ma.loadtest;

import kurz.ma.examples.simple.Main4Loop;
import kurz.ma.examples.simple.Main;
import kurz.ma.examples.simple.Main2;
import kurz.ma.examples.simple.Main3;
import kurz.ma.examples.simple.Main5Recursion;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Driver {

    private static int processesStarted = 0;

    public static void main(String[] args) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        final Runnable exec = getExec(args[0]);
        scheduledExecutorService.scheduleAtFixedRate(exec, 0, 1500, TimeUnit.MILLISECONDS);
        scheduledExecutorService.schedule(scheduledExecutorService::shutdown, 10, TimeUnit.SECONDS);
    }

    private static Runnable getExec(final String arg) {
        switch (arg) {
            case "1":
                return Driver::exec;
            case "2":
                return Driver::exec2;
            case "3":
                return Driver::exec3;
            case "4":
            case "loop":
                return Driver::exec4Loop;
            case "5":
            case "recursion":
                return Driver::exec5Recursion;
            default:
                throw new RuntimeException("Unknown input: " + arg);
        }
    }

    private static void exec() {
        System.out.println("Processes start: " + ++processesStarted);
        Main.main(new String[0]);
    }
    private static void exec2() {
        System.out.println("Processes start: " + ++processesStarted);
        Main2.main(new String[0]);
    }
    private static void exec3() {
        System.out.println("Processes start: " + ++processesStarted);
        Main3.main(new String[0]);
    }
    private static void exec4Loop() {
        System.out.println("Processes start: " + ++processesStarted);
        Main4Loop.main(new String[0]);
    }
    private static void exec5Recursion() {
        System.out.println("Processes start: " + ++processesStarted);
        Main5Recursion.main(new String[0]);
    }

    public static void busyWait(final int waitTimeMillis) {
        final Instant start = Instant.now();
        final Instant targetTime = start.plus(waitTimeMillis, ChronoUnit.MILLIS);
        Instant end = Instant.now();
        while (end.isBefore(targetTime)) {
            end = Instant.now();
        }
    }
}
