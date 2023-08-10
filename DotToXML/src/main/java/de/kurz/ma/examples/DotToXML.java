package de.kurz.ma.examples;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.kurz.ma.examples.callTree.CallTreeCreator;
import de.kurz.ma.examples.callTree.model.CallTree;
import de.kurz.ma.examples.cli.CliParameters;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class DotToXML {

    public static final CliParameters PARAMETERS = new CliParameters();

    public static void main(String[] args) throws IOException {
        System.out.println("Starting Dot to XML toolkit.");

        final JCommander jc = JCommander.newBuilder()
                .addObject(PARAMETERS)
                .args(args)
                .build();

        if (PARAMETERS.isHelp()) {
            jc.usage();
        }

        if (PARAMETERS.getInputFiles().isEmpty() && PARAMETERS.getInputDirectories().isEmpty()) {
            throw new ParameterException("At least one of the '--file' or '--directory' is required. Given was none.");
        }

        // handle Files
        PARAMETERS.getInputFiles().forEach(DotToXML::doWriteXml);

        // handle directories
        for (Path d : PARAMETERS.getInputDirectories()) {
            final List<Path> paths = getFilesInDirectory(d);
            paths.forEach(DotToXML::doWriteXml);
        }


        System.out.println("Dot to XML toolkit has successfully finished.");
    }

    private static List<Path> getFilesInDirectory(final Path d) {
        final File[] files = requireNonNull(d.toFile().listFiles());
        final List<Path> paths = Arrays.stream(files)
                .filter(File::isFile)
                .filter(f -> f.getName().endsWith(".dot"))
                .map(File::toPath).collect(Collectors.toList());

        if (PARAMETERS.isRecursive()) {
            final List<Path> directoryPaths = Arrays.stream(files)
                    .filter(File::isDirectory)
                    .map(File::toPath)
                    .map(DotToXML::getFilesInDirectory)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            paths.addAll(directoryPaths);
        }
        return paths;
    }

    private static void doWriteXml(final Path f) {
        try {
            final CallTree callTree = getCallTree(f);
            writeXml(callTree, f, PARAMETERS.getOutputDirectory());
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
