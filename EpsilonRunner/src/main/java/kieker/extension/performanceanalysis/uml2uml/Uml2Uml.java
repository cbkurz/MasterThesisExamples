package kieker.extension.performanceanalysis.uml2uml;

import kieker.extension.performanceanalysis.epsilon.EmfModelBuilder;
import kieker.extension.performanceanalysis.epsilon.Util;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static kieker.extension.performanceanalysis.epsilon.Util.getUmlUri;

public class Uml2Uml implements Runnable {

    private final EmfModel umlSourceModel;
    private final EmfModel transformationModel;
    private final EmfModel umlFutureModel;
    private final Path script;

    public Uml2Uml(final Path umlSourceModel, final Path transformationModel, final Path umlFutureModel) {
        this.script = Util.getResource("Uml2Uml/Uml2Uml.etl");
//        this.umlSourceModel = Util.getUmlModel(umlSourceModel, "UML", true);
        this.umlSourceModel = getSourceUml(umlSourceModel);;
        this.transformationModel = getTransformationModel(transformationModel);
        this.umlFutureModel = getFuml(umlFutureModel);
    }

    private static EmfModel getSourceUml(final Path umlSourceModel) {
        return EmfModelBuilder.getInstance()
                .setName("UML")
                .setMetaModel(getUmlUri())
                .setModel(umlSourceModel)
                .setReadOnly(true)
                .build();
    }

    private static EmfModel getFuml(final Path umlFutureModel) {
        return EmfModelBuilder.getInstance()
                .setName("FUML")
                .setMetaModel(getUmlUri())
                .setModel(umlFutureModel)
                .setReadOnLoad(false)
                .setStoreOnDisposal(true)
                .build();
    }

    private static EmfModel getTransformationModel(final Path transformationModel) {
        return EmfModelBuilder.getInstance()
                .setName("UmlTransformation")
                .setAlias("UT")
                .setMetaModel("UmlTransformation.ecore")
                .setModel(transformationModel)
                .setReadOnly(true)
                .build();
    }

    @Override
    public void run() {
        final EtlRunConfiguration runConfiguration = EtlRunConfiguration.Builder()
                .withScript(script)
                .withModel(umlSourceModel)
                .withModel(transformationModel)
                .withModel(umlFutureModel)
                .withProfiling()
                .build();
        runConfiguration.run();
        umlFutureModel.dispose();
    }

    private static Model createModel(final String name) {
        final Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);
        return model;
    }

    private static Path saveModel(Model model, Path targetFile) {
        final ResourceSet resourceSet = new ResourceSetImpl();

        UMLResourcesUtil.init(resourceSet);

        Resource resource = resourceSet.createResource(URI.createURI(targetFile.toString()));
        resource.getContents().add(model);

        // And save
        try {
            resource.save(null); // no save options needed
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return targetFile;
    }

}
