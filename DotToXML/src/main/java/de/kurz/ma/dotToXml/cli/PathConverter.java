package de.kurz.ma.dotToXml.cli;

import com.beust.jcommander.IStringConverter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathConverter implements IStringConverter<Path> {
    @Override
    public Path convert(final String value) {
        return Paths.get(value);
    }
}
