package kieker.extension.epsilon.runner;

import org.eclipse.epsilon.emc.emf.EmfModel;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Util {


    public static Path getResource(final String path) {
        try {
            return Paths.get(Util.class.getClassLoader().getResource(path).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path getEtlScript(final String path) {
        return getResource("epsilon").resolve("etl").resolve(path);
    }
    public static Path getEolScript(final String path) {
        return getResource("epsilon").resolve("eol").resolve(path);
    }

    public static EmfModel getUmlModel(final Path umlModel, final String name) {
        final URI metaModel;
        try {
            metaModel = new URI("http://www.eclipse.org/uml2/5.0.0/UML");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return EmfModelBuilder.getInstance()
                .setName(name)
                .setMetaModel(metaModel)
                .setModel(umlModel)
                .build();
    }
}
