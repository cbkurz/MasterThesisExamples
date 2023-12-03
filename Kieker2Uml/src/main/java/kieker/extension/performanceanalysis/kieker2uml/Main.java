package kieker.extension.performanceanalysis.kieker2uml;

import kieker.extension.performanceanalysis.kieker2uml.teetime.Kieker2UmlTeeTimeService;

import java.io.IOException;

public class Main {

    private Main() {
    }

    public static void main(final String[] args) throws IOException {
        final Kieker2UmlTeeTimeService service = Kieker2UmlTeeTimeService.getInstance();
        System.exit(service.run("Kieker2Uml", "kieker-2-uml", args, service.getParameters()));
    }
}
