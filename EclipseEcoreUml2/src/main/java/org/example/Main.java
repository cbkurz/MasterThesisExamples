package org.example;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import java.nio.file.Paths;

public class Main {
    private URI typesUri = null;

    public static void main(String[] args) {
        Model m = new Main().getModel(Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams.uml").toAbsolutePath().toString());
        System.out.println(m.getName());
    }

    public Model getModel(String pathToModel) {

        typesUri = URI.createFileURI(pathToModel);
        ResourceSet set = new ResourceSetImpl();

        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        set.createResource(typesUri);
        Resource r = set.getResource(typesUri, true);

        Model m = (Model) EcoreUtil.getObjectByType(r.getContents(), UMLPackage.Literals.MODEL);

        return m;
    }
}