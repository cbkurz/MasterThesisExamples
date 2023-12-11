package de.loadtest;

import kieker.examples.monitoring.application.Main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Driver {

    public static void main(String[] args) {
        final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
        scheduledExecutorService.scheduleAtFixedRate(Driver::bookstoreExecution, 0, 2, TimeUnit.SECONDS);
        scheduledExecutorService.schedule(scheduledExecutorService::shutdown, 20, TimeUnit.SECONDS);
    }

    private static void bookstoreExecution() {
        System.out.println("Starting the Bookstore");
        Main.main(new String[0]);
    }
}
