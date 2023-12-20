package kurz.ma.examples.simple;

import kurz.ma.loadtest.Driver;

public class Worker {

    public static void work2() {
        Driver.busyWait(500);
    }
    public static void work3() {
        Driver.busyWait(200);
        Main3.doSomething();
    }
}
