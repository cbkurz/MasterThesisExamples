package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Uml2Support {

    public static void main(String[] args) {
        Model m = loadModel(Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams.uml"));
        System.out.println(m.getName());
        saveModel(m, Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams2.uml"));
    }

    public static Model loadModel(Path pathToModel) {

        URI typesUri = URI.createFileURI(pathToModel.toString());
        ResourceSet set = new ResourceSetImpl();

        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        set.createResource(typesUri);
        Resource r = set.getResource(typesUri, true);

        return (Model) EcoreUtil.getObjectByType(r.getContents(), UMLPackage.Literals.MODEL);
    }

    public static Path saveModel(Model model, Path directory) {
        final ResourceSet resourceSet = new ResourceSetImpl();

        UMLResourcesUtil.init(resourceSet);

        Resource resource = resourceSet.createResource(URI.createURI(directory.toString()).appendSegment("model-" + model.getLabel()).appendFileExtension(UMLResource.FILE_EXTENSION));
        resource.getContents().add(model);

        // And save
        try {
            resource.save(null); // no save options needed
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return directory;
    }

    public static Model createModel(final String name) {
        final Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);
        return model;
    }

    public static void addInteractionToModel(final Model model, final MessageTrace messageTrace) {
        Uml2Interactions2.addInteractionToModel(model, messageTrace);
    }
}
