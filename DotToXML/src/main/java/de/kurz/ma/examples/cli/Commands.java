package de.kurz.ma.examples.cli;

import com.beust.jcommander.Parameter;

import java.nio.file.Path;
import java.util.List;

public class Commands {

    @Parameter(names = {"-f", "--file"},
            variableArity = true,
            description = "A file or list of files to parse to XML. The output file has the same name as the input but the extension is '.model'",
            converter = PathConverter.class,
            validateWith = FileIsPresentValidator.class
    )
    private List<Path> inputFiles;

    @Parameter(names = {"-d", "--directory"},
            variableArity = true,
            description = "A directory or list of directories in which all files will be converted. The name of the output is the same as the input but with the extension '.model'",
            converter = PathConverter.class,
            validateWith = FileIsPresentValidator.class
    )
    private List<Path> inputDirectories;

    @Parameter(names = {"-o", "--output"},
            description = "The directory to which the output is written. If this is not set, the output will be written to the current directory.",
            converter = PathConverter.class
    )
    private Path outputDirectory;

    @Parameter(names = "--help", help = true)
    private boolean help;

    public List<Path> getInputFiles() {
        return inputFiles;
    }

    public List<Path> getInputDirectories() {
        return inputDirectories;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public boolean isHelp() {
        return help;
    }

}
