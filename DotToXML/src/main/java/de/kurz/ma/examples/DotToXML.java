package de.kurz.ma.examples;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.kurz.ma.examples.callTree.CallTreeCreator;
import de.kurz.ma.examples.callTree.model.CallTree;
import de.kurz.ma.examples.cli.Commands;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DotToXML {

    public static final Commands COMMANDS = new Commands();

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Dot to XML toolkit.");

        JCommander.newBuilder()
                .addObject(COMMANDS)
                .args(args)
                .build();

        if (COMMANDS.getInputFiles().isEmpty() && COMMANDS.getInputDirectories().isEmpty()) {
            throw new ParameterException("At least one of the '--file' or '--directory' is required. Given was non.");
        }

        // handle Files
        COMMANDS.getInputFiles().forEach(DotToXML::doWriteXml);

        // handle directories
        for (Path d : COMMANDS.getInputDirectories()) {
            final File[] files = requireNonNull(d.toFile().listFiles());
            Arrays.stream(files)
                    .filter(File::isFile)
                    .filter(f -> f.getName().endsWith(".dot"))
                    .map(File::toPath).collect(Collectors.toList())
                    .forEach(DotToXML::doWriteXml);
        }


        System.out.println("Dot to XML toolkit has successfully finished.");
    }

    private static void doWriteXml(final Path f) {
        try {
            final CallTree callTree = getCallTree(f);
            writeXml(callTree, f, COMMANDS.getOutputDirectory());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeXml(final CallTree callTree, final Path inputPath, final Path output) {
        if (output.toFile().isFile()) {
            throw new IllegalArgumentException("Output must be a folder but was a file: " + output);
        }
        final String fileName = inputPath.getFileName().toString() + ".model";

        final Path target = output.resolve(fileName);
        System.out.println("Writing to File: " + target);
        XmlSupport.writeXml(target, callTree);
    }

    private static CallTree getCallTree(final Path inputPath) throws IOException {
        System.out.println("Reading from File: " + inputPath);
        return CallTreeCreator.createCallTree(inputPath);
    }

}
