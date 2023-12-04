package kieker.extension.performanceanalysis.epsilon;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Strings;
import kieker.extension.performanceanalysis.cli.Uml2LqnCli;
import kieker.extension.performanceanalysis.cli.Uml2PlantUmlCli;
import kieker.extension.performanceanalysis.cli.Uml2UmlCli;
import kieker.extension.performanceanalysis.uml2plantuml.Uml2PlantUml;
import kieker.extension.performanceanalysis.uml2uml.Uml2Uml;


public class EpsilonRunner {
    public static void main(String[] args) {

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
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            jc.usage();
            return;
        }
        String parsedCommand = jc.getParsedCommand();

        if (Strings.isStringEmpty(parsedCommand)) {
            jc.usage();
            return;
        }

        System.out.println("Command: " + parsedCommand);
        switch (parsedCommand) {
            case "Uml2PlantUml":
                new Uml2PlantUml(uml2PlantUmlCli.getUmlPath(), uml2PlantUmlCli.getOutputPath()).run();
                break;
            case "Uml2Uml":
                new Uml2Uml(uml2UmlCli.getUmlPath(), uml2UmlCli.getTransformationPath(), uml2UmlCli.getUmlOutput()).run();
            default:
                throw new RuntimeException("Unknown command: " + parsedCommand);
        }
    }
}