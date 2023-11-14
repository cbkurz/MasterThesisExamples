package de.kurz.ma.epsilon.runner;

import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;

import java.nio.file.Path;


public class EpsilonRunner {
    final static Path metaModels = Util.getResource("metamodels");
    public static void main(String[] args) throws Exception {
        final Path models = Util.getResource("models");
        final Path scripts = Util.getResource("epsilon");
        final EmfModel sourceModel = getSource(models);
        final EmfModel targetModel = getTarget(models);

        final EtlRunConfiguration runConfig = EtlRunConfiguration.Builder()
                .withScript(scripts.resolve("Transformation.etl"))
                .withModel(sourceModel)
                .withModel(targetModel)
                .withParameter("parameterPassedFromJava", "Hello from java")
                .withProfiling()
                .build();

        runConfig.run();
    }

    private static EmfModel getSource(final Path models) {
        return EmfModelBuilder.getInstance()
                .setName("Source")
                .setMetaModel(metaModels.resolve("Tree.ecore"))
                .setModel(models.resolve("Tree.xmi"))
                .build();
    }

    private static EmfModel getTarget(final Path models)  {
        return EmfModelBuilder.getInstance()
                .setName("Target")
                .setMetaModel(metaModels.resolve("Tree.ecore"))
                .setModel(models.resolve("Copy.xmi"))
                .setReadOnLoad(false)
                .build();
    }

}