package kurz.ma.examples.simple;

import kurz.ma.loadtest.Driver;

public class Main3 {
    public static void main(String[] args) {
        Driver.busyWait(200);
        doSomething();
        Worker.work3();
    }

    public static void doSomething() {
        Driver.busyWait(300);
    }

}
