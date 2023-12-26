package kurz.ma.examples.simple;

import static kurz.ma.loadtest.Driver.busyWait;

public class Loop {
    public void loop(final int iterations, final int millies) {
        for (int i = 0; i < iterations; i++) {
            workLoop(millies);
        }
    }

    public void workLoop(final int millies) {
        busyWait(millies);
    }

}
