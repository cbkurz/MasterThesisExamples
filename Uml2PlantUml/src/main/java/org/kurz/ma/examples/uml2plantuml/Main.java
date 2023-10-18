package org.kurz.ma.examples.uml2plantuml;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class Main {
    public static void main(String[] args) {
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

}