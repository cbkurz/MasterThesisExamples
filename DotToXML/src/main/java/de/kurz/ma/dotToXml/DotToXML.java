package de.kurz.ma.dotToXml;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.kurz.ma.dotToXml.callTree.CallTreeCreator;
import de.kurz.ma.dotToXml.callTree.model.CallTree;
import de.kurz.ma.dotToXml.cli.CliParameters;
import de.kurz.ma.dotToXml.xml.XmlSupport;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
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
        PARAMETERS.getInputFiles().forEach(f -> dotToXml(f, PARAMETERS.getOutputDirectory()));

        // handle directories
        for (Path d : PARAMETERS.getInputDirectories()) {
            final List<Path> paths = getFilesInDirectory(d);
            paths.forEach(f -> {
                final Path o = getOutputDir(PARAMETERS.getOutputDirectory(), d, f);
                dotToXml(f, o);
            });
        }


        System.out.println("Dot to XML toolkit has successfully finished.");
    }

    /**
     * Builds the tree substructure from the file under the inputDirectory under the outputDirectory if recursive == true.
     *
     * @param outputDirectory - the directory to which the target will be written.
     * @param inputDirectory  - the directory under which the file is found.
     * @param file            - the file which is under the input directory.
     * @return If recursive == false the outputDirectory is returned
     * else the outputDirectory including the tree substructure of the file from input directory.
     */
    private static Path getOutputDir(final Path outputDirectory, final Path inputDirectory, final Path file) {
        if (!PARAMETERS.isRecursive()) {
            return outputDirectory;
        }

        final String name = inputDirectory.toFile().getName();
        List<String> names = new ArrayList<>();
        Path currentDir = file.getParent();

        // get all the folder names in an ordered list.
        while (!name.equals(currentDir.toFile().getName())) {
            names.add(currentDir.toFile().getName());
            currentDir = currentDir.getParent();
        }

        // walk through the list backwards.
        Path outputWithSubtree = outputDirectory;
        for (int i = names.size() - 1; i >= 0; i--) {
            outputWithSubtree = outputWithSubtree.resolve(names.get(i));
        }
        return outputWithSubtree;
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
                    .map(DotToXML::getFilesInDirectory) // recursion
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            paths.addAll(directoryPaths);
        }
        return paths;
    }

    private static void dotToXml(final Path f, final Path outputDirectory) {
        try {
            final CallTree callTree = getCallTree(f);
            writeXml(callTree, f, outputDirectory);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void writeXml(final CallTree callTree, final Path inputPath, final Path output) {
        if (output.toFile().isFile()) {
            throw new IllegalArgumentException("Output must be a folder but was a file: " + output);
        }
        if (!output.toFile().exists()) {
            if (!output.toFile().mkdirs()) {
                throw new IllegalArgumentException("Output does not exist and cannot be created: " + output);
            }
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
