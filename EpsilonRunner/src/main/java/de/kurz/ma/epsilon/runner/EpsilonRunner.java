package de.kurz.ma.epsilon.runner;

import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.exceptions.models.EolModelLoadingException;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;

import java.nio.file.Path;


public class EpsilonRunner {
    final static Path metaModels = Util.getResource("metamodels");
    public static void main(String[] args) throws Exception {
        final Path models = Util.getResource("models");
        final Path scripts = Util.getResource("epsilon");


        final EmfModel sourceModel = getModel(models);

        final EmfModel targetModel = getEmfModel(models);

        final EtlRunConfiguration runConfig = EtlRunConfiguration.Builder()
                .withScript(scripts.resolve("Transformation.etl"))
                .withModel(sourceModel)
                .withModel(targetModel)
                .withParameter("parameterPassedFromJava", "Hello from pre")
                .withProfiling()
                .build();

        runConfig.run();
    }

    private static EmfModel getModel(final Path models) throws EolModelLoadingException {
        final EmfModel sourceModel = new EmfModel();
        final StringProperties sourceProperties = new StringProperties();
        sourceProperties.setProperty(EmfModel.PROPERTY_NAME, "Source");
        sourceProperties.setProperty(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI, metaModels.resolve("Tree.ecore").toAbsolutePath().toUri().toString());
        sourceProperties.setProperty(EmfModel.PROPERTY_MODEL_URI, models.resolve("Tree.xmi").toAbsolutePath().toUri().toString());
        sourceModel.load(sourceProperties);
        return sourceModel;
    }

    private static EmfModel getEmfModel(final Path models) throws EolModelLoadingException {
        final String treeMM = metaModels.resolve("Tree.ecore").toAbsolutePath().toUri().toString();
        final EmfModel targetModel = new EmfModel();
        final StringProperties targetProperties = new StringProperties();
        targetProperties.setProperty(EmfModel.PROPERTY_NAME, "Target");
        targetProperties.setProperty(EmfModel.PROPERTY_FILE_BASED_METAMODEL_URI, treeMM);
        targetProperties.setProperty(EmfModel.PROPERTY_MODEL_URI, models.resolve("Copy.xmi").toAbsolutePath().toUri().toString());
        targetProperties.setProperty(EmfModel.PROPERTY_READONLOAD, "false");
        targetProperties.setProperty(EmfModel.PROPERTY_STOREONDISPOSAL, "true");
        targetModel.load(targetProperties);
        return targetModel;
    }

}