package de.kurz.ma.examples;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class DotToXML {
    public static void main(String[] args) {
        System.out.println("Starting Dot to XML toolkit.");

        final String input = getArgument(args, "-i");
        Objects.requireNonNull(input, "input must be set.");
        if (!input.endsWith(".dot")) {
            throw new IllegalArgumentException("Please provide a '.dot' file for the conversion to XML.");
        }

        final Path inputPath = Paths.get(input);
        if (!inputPath.toFile().isFile()) {
            throw new IllegalArgumentException(String.format("The given input '%s' is not a file.", input));
        }
    }

    private static String getArgument(final String[] args, final String argumentName) {
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if (arg.equals(argumentName)) {
                return args[i + 1];
            }
        }
        return null;
    }
}
