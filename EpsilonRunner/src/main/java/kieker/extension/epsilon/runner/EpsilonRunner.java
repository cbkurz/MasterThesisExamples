package kieker.extension.epsilon.runner;

import com.beust.jcommander.JCommander;
import kieker.extension.epsilon.runner.cli.Uml2LqnCli;
import kieker.extension.epsilon.runner.cli.Uml2PlantUmlCli;
import kieker.extension.epsilon.runner.cli.Uml2UmlCli;
import kieker.extension.epsilon.runner.uml2plantuml.Uml2PlantUml;
import org.eclipse.epsilon.emc.emf.EmfModel;


public class EpsilonRunner {
    public static void main(String[] args) throws Exception {

        final Uml2PlantUmlCli uml2PlantUmlCli = new Uml2PlantUmlCli();
        final Uml2LqnCli uml2LqnCli = new Uml2LqnCli();
        final Uml2UmlCli uml2UmlCli = new Uml2UmlCli();

        JCommander jc = JCommander.newBuilder()
                .addCommand(uml2PlantUmlCli)
                .addCommand(uml2LqnCli)
                .addCommand(uml2UmlCli)
                .build();
        try {
            jc.parse(args);
        } catch (Exception e) {
            jc.usage();
            return;
        }
        String parsedCommand = jc.getParsedCommand();

        System.out.println("Command: " + parsedCommand);
        switch (parsedCommand) {
            case "Uml2PlantUml":
                new Uml2PlantUml(uml2PlantUmlCli.getUmlPath(), uml2PlantUmlCli.getOutputPath()).run();
                break;
            default:
                throw new RuntimeException("Unknown command: " + parsedCommand);
        }

/*
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
*/
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