package kieker.extension.performanceanalysis.uml2plantuml;

import kieker.extension.performanceanalysis.epsilon.Util;
import org.eclipse.epsilon.egl.launch.EgxRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class Uml2PlantUml implements Runnable {

    private final EmfModel umlModel;
    private final Path driver;
    private final Path output;

    public Uml2PlantUml(final Path umlModel, final Path output) {
        requireNonNull(umlModel, "umlModel");
        requireNonNull(output, "output");
        this.umlModel = Util.getUmlModel(umlModel, "uml", false);
        this.driver = Util.getResource("Uml2PlantUml/Driver.egx");
        this.output = output;
    }

    @Override
    public void run() {
        final EgxRunConfiguration runConfiguration = EgxRunConfiguration.Builder()
                .withScript(driver)
                .withModel(umlModel)
                .withOutputRoot(output)
                .withProfiling()
                .build();
        runConfiguration.run();
    }
}
