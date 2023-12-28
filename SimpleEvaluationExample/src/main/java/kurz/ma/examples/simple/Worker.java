package kurz.ma.examples.simple;

import static kurz.ma.loadtest.Driver.busyWait;

public class Worker {

    public static void work2() {
        busyWait(500);
    }
    public static void work3() {
        busyWait(200);
        Main3.doSomething();
    }

    public static void work6(final int millis) {
        busyWait(millis);
    }
}
