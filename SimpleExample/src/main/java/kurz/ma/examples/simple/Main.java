package kurz.ma.examples.simple;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class Main {
    public static void main(String[] args) {
        final Instant start = Instant.now();
        final Instant targetTime = start.plus(1000, ChronoUnit.MILLIS);
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
