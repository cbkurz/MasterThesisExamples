package kurz.ma.examples.simple;

import static kurz.ma.loadtest.Driver.busyWait;

public class Loop {
    public void loop(final int iterations, final int millis) {
        for (int i = 0; i < iterations; i++) {
            busyWait(millis);
        }
    }
    public void loop6(final int iterations, final int millis) {
        for (int i = 0; i < iterations; i++) {
            if (i % 2 == 0) {
                waitIfElse(millis);
            } else {
                waitIfElse(millis / 3);
            }
        }
    }

    private void waitIfElse(final int millis) {
        busyWait(millis);
    }
}
