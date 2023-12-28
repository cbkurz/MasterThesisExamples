package kurz.ma.examples.simple;

import static kurz.ma.loadtest.Driver.busyWait;

public class Recursion {

    public void recursion(final int iterator, final int millis) {
        if (iterator - 1 > 0) {
            busyWait(millis);
            recursion(iterator - 1, millis);
        }
    }
    public void recursion6(final int iterator, final int millis) {
        if (iterator - 1 > 0) {
            busyWait(millis);
            recursion6(iterator - 1, millis);
        } else {
            if (millis == 50) {
                Worker.work6(50);
            }
        }
    }
}
