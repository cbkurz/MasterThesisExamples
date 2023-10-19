package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class UmlUtil {
    public static final EClass PACKAGE_E_CLASS = UMLFactory.eINSTANCE.createPackage().eClass();

    static Package getPackagedElement(final Model model, final String packageName) {
        return (Package) model.getPackagedElements().stream()
                .filter(p -> p.getName().equals(packageName))
                .findFirst()
                .orElseGet(() -> model.createPackagedElement(packageName, PACKAGE_E_CLASS));
    }

    static void setAnnotationDetail(final Element element, final String annotationName, final String key, final String value) {
        final EAnnotation eAnnotation = element.getEAnnotations().stream()
                .filter(a -> a.getSource().equals(annotationName))
                .findFirst()
                .orElseGet(() -> element.createEAnnotation(annotationName));

        final EMap<String, String> details = eAnnotation.getDetails();
        if (details.containsKey(key)) {
            details.removeKey(key);
            details.put(key, value);
        } else {
            details.put(key, value);
        }
    }

    public static Path saveModel(Model model, Path targetFile) {
        final ResourceSet resourceSet = new ResourceSetImpl();

        UMLResourcesUtil.init(resourceSet);

        Resource resource = resourceSet.createResource(URI.createURI(targetFile.toString()));
        resource.getContents().add(model);

        // And save
        try {
            resource.save(null); // no save options needed
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return targetFile;
    }

    public static Model loadModel(Path pathToModel) {

        // unclear why this is necessary,
        // however if this isn't initialized here again,
        // the program fails since the model will not be loaded when it is created by the InputModelValidator.class.
        URI typesUri = URI.createFileURI(pathToModel.toString());
        ResourceSet set = new ResourceSetImpl();

        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        set.createResource(typesUri);
        Resource r = set.getResource(typesUri, true);

        return requireNonNull((Model) EcoreUtil.getObjectByType(r.getContents(), UMLPackage.Literals.MODEL));
    }

    public static Model createModel(final String name) {
        final Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);
        return model;
    }
}
