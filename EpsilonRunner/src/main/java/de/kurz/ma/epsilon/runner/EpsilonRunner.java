package de.kurz.ma.epsilon.runner;

import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.etl.launch.EtlRunConfiguration;


public class EpsilonRunner {
    public static void main(String[] args) throws Exception {
        final EmfModel sourceModel = getSource();
        final EmfModel targetModel = getTarget();

        final EolModule module = new EolModule();
        module.parse(Util.getEolScript("Operations.eol"));
        final EtlRunConfiguration runConfig = EtlRunConfiguration.Builder()
                .withScript(Util.getEtlScript("Transformation.etl"))
                .withModel(sourceModel)
                .withModel(targetModel)
                .withParameter("parameterPassedFromJava", "Hello from java")
                .withProfiling()
                .build();

        runConfig.run();
    }

    private static EmfModel getSource() {
        return EmfModelBuilder.getInstance()
                .setName("Source")
                .setMetaModel("Tree.ecore")
                .setModel("Tree.xmi")
                .build();
    }

    private static EmfModel getTarget()  {
        return EmfModelBuilder.getInstance()
                .setName("Target")
                .setMetaModel("Tree.ecore")
                .setModel("Copy.xmi")
                .setReadOnLoad(false)
                .build();
    }

}