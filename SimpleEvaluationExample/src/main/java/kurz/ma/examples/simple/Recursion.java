package kurz.ma.examples.simple;

import static kurz.ma.loadtest.Driver.busyWait;

public class Recursion {

    public void recursion(final int iterator, final int millis) {
        if (iterator - 1 > 0) {
            busyWait(millis);
            recursion(iterator - 1, millis);
        }
    }
}
