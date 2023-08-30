package org.kurz.ma.examples.kieker2uml;

import org.apache.commons.io.FileUtils;
import org.kurz.ma.examples.kieker2uml.teetime.Kieker2UmlTeeTimeService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Kieker2UmlMain {

    private Kieker2UmlMain() {
    }

    public static void main(final String[] args) throws IOException {
        final File file = FileUtils.getFile("output.txt");
        FileUtils.write(file, "start at " + Instant.now(), StandardCharsets.UTF_8, false);
        final Kieker2UmlTeeTimeService service = Kieker2UmlTeeTimeService.getInstance();
        System.exit(service.run("Kieker2Uml", "kieker-2-uml", args, service.getParameters()));
    }
}
