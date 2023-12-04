package kieker.extension.performanceanalysis.uml2uml;

import com.beust.jcommander.ParameterException;
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
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

public class Uml2Uml implements Runnable {

    private final EmfModel umlSourceModel;
    private final EmfModel transformationModel;
    private final EmfModel umlFutureModel;
    private final Path script;

    public Uml2Uml(final Path umlSourceModel, final Path transformationModel, final Path umlFutureModel) {
        this.script = Util.getResource("Uml2Uml/Uml2Uml.etl");
        this.umlSourceModel = Util.getUmlModel(umlSourceModel, "UML", true);
        this.transformationModel = getTransformationModel(transformationModel);
        this.umlFutureModel = getFuml(umlFutureModel);
    }

    private static EmfModel getFuml(final Path umlFutureModel) {
        if (!umlFutureModel.toString().endsWith(UMLResource.FILE_EXTENSION)) {
            throw new ParameterException("File does not follow the UML convention to end with '.uml': " + umlFutureModel);
        }


        if (!umlFutureModel.toFile().exists()) {
            try {
                final Model model = createModel(umlFutureModel.getFileName().toString());
                saveModel(model, umlFutureModel);
            } catch (Exception e) {
                final String message = "File does not exist for paramter '" + umlFutureModel + "' " +
                        "and creating the respective model has failed: " + umlFutureModel;
                throw new ParameterException(message, e);
            }
        }

        return Util.getUmlModel(umlFutureModel, "FUML", false);
    }

    private static EmfModel getTransformationModel(final Path transformationModel) {
        return EmfModelBuilder.getInstance()
                .setName("UT")
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
