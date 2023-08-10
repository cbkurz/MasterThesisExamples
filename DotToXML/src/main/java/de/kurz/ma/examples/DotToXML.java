package de.kurz.ma.examples;

import de.kurz.ma.examples.callTree.CallTreeCreator;
import de.kurz.ma.examples.callTree.model.CallTree;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class DotToXML {
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Dot to XML toolkit.");

        final CallTree callTree = getCallTree(args);

        writeXml(args, callTree);

        System.out.println("Dot to XML toolkit has successfully finished.");
    }

    private static void writeXml(final String[] args, final CallTree callTree) {
        final Path inputPath = getInputPath(args);
        final Path output = getArgument(args, "-o")
                .map(Paths::get)
                .orElseGet(() -> Paths.get(""));
        if (output.toFile().isFile()) {
            throw new IllegalArgumentException("Output must be a folder but was a file: " + output);
        }
        final String fileName = inputPath.getFileName().toString() + ".model";

        final Path target = output.resolve(fileName);
        System.out.println("Writing to File: " + target);
        XmlSupport.writeXml(target, callTree);
    }

    private static CallTree getCallTree(final String[] args) throws IOException {
        final Path inputPath = getInputPath(args);
        System.out.println("Reading from File: " + inputPath);
        return CallTreeCreator.createCallTree(inputPath);
    }

    private static Path getInputPath(final String[] args) {
        final String input = getArgument(args, "-i").orElseThrow();
        if (!input.endsWith(".dot")) {
            throw new IllegalArgumentException("Please provide a '.dot' file for the conversion to XML.");
        }

        final Path inputPath = Paths.get(input);
        if (!inputPath.toFile().isFile()) {
            throw new IllegalArgumentException(String.format("The given input '%s' is not a file.", inputPath.toAbsolutePath()));
        }
        return inputPath;
    }

    private static Optional<String> getArgument(final String[] args, final String argumentName) {
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.equals(argumentName)) {
                return Optional.of(args[i + 1]);
            }
        }
        return Optional.empty();
    }
}
