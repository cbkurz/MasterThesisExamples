package kurz.ma.examples.simple;

import kurz.ma.loadtest.Driver;

public class Main2 {
    public static void main(String[] args) {
        Driver.busyWait(500);
        Worker.work2();
    }

}
