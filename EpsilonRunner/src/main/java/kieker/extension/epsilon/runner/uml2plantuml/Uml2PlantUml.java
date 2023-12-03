package kieker.extension.epsilon.runner.uml2plantuml;

import kieker.extension.epsilon.runner.EmfModelBuilder;
import kieker.extension.epsilon.runner.Util;
import org.eclipse.epsilon.egl.launch.EgxRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Uml2PlantUml implements Runnable {

    private final EmfModel umlModel;
    private final Path driver;

    public Uml2PlantUml(final Path umlModel) {
        this.umlModel = getUmlModel(umlModel);
        this.driver = Util.getResource("uml2plantUml/Driver.egx");
    }

    private EmfModel getUmlModel(final Path umlModel) {
        final URI metaModel;
        try {
            metaModel = new URI("http://www.eclipse.org/uml2/5.0.0/UML");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return EmfModelBuilder.getInstance()
                .setName("uml")
                .setMetaModel(metaModel)
                .setModel(umlModel)
                .build();
    }

    @Override
    public void run() {
        final EgxRunConfiguration runConfiguration = EgxRunConfiguration.Builder()
                .withScript(driver)
                .withModel(umlModel)
                .withOutputRoot(Paths.get("egxOutput"))
                .withProfiling()
                .build();
        runConfiguration.run();
    }
}
