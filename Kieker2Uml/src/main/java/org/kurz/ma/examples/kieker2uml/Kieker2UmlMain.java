package org.kurz.ma.examples.kieker2uml;

import org.kurz.ma.examples.kieker2uml.teetime.Kieker2UmlTeeTimeService;

public class Kieker2UmlMain {

    private Kieker2UmlMain() {
    }

    public static void main(final String[] args) {
        final Kieker2UmlTeeTimeService service = Kieker2UmlTeeTimeService.getInstance();
        System.exit(service.run("Kieker2Uml", "kieker-2-uml", args, service.getParameters()));
    }
}
