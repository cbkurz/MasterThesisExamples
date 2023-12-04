package kieker.extension.performanceanalysis.uml2lqn;

import kieker.extension.performanceanalysis.epsilon.EmfModelBuilder;
import kieker.extension.performanceanalysis.epsilon.Util;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;

import java.nio.file.Path;

import static kieker.extension.performanceanalysis.epsilon.Util.getUmlUri;

public class Uml2Lqn implements Runnable {

    private final Path script;

    private final EmfModel umlModel;
    private final EmfModel lqnModel;
    public Uml2Lqn(final Path umlModel, final Path lqnModel) {
        this.script = Util.getResource("Uml2Lqn/Uml2Lqn.etl");
        this.umlModel = getUmlModel(umlModel);
        this.lqnModel = getLqnModel(lqnModel);
    }

    private EmfModel getLqnModel(final Path lqnModel) {
        return EmfModelBuilder.getInstance()
                .setName("LQN")
                .setMetaModel("lqn.ecore")
                .setModel(lqnModel)
                .setReadOnLoad(false)
                .setStoreOnDisposal(true)
                .build();
    }

    private EmfModel getUmlModel(final Path umlModel) {
        return EmfModelBuilder.getInstance()
                .setName("UML")
                .setMetaModel(getUmlUri())
                .setModel(umlModel)
                .setReadOnly(true)
                .setStoreOnDisposal(false)
                .build();
    }

    @Override
    public void run() {
        final EtlRunConfiguration runConfiguration = EtlRunConfiguration.Builder()
                .withScript(script)
                .withModel(umlModel)
                .withModel(lqnModel)
                .withProfiling()
                .build();
        runConfiguration.run();
        lqnModel.dispose();

    }
}
