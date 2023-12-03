package kieker.extension.epsilon.runner.uml2uml;

import kieker.extension.epsilon.runner.Util;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;

import java.nio.file.Path;

public class Uml2Uml implements Runnable {

    private final EmfModel umlSourceModel;
    private final EmfModel umlTargetModel;
    private final Path driver;
    private final Path output;

    public Uml2Uml(final Path umlSourceModel, final Path umlTargetModel, final Path output) {
        this.umlSourceModel = Util.getUmlModel(umlSourceModel, "UML");
        this.umlTargetModel = Util.getUmlModel(umlTargetModel, "FUML");
        this.driver = Util.getResource("Uml2Uml/Uml2Uml.etl");
        this.output = output;
    }

    @Override
    public void run() {
        final EtlRunConfiguration runConfiguration = EtlRunConfiguration.Builder()
                .withScript(driver)
                .withModel(umlSourceModel)
                .withModel(umlTargetModel)
                .withProfiling()
                .build();
        runConfiguration.run();
    }
}
