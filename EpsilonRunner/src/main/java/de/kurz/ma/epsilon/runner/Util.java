package de.kurz.ma.epsilon.runner;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {


    public static Path getResource(final String path)  {
        try {
            return Paths.get(Util.class.getClassLoader().getResource(path).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
