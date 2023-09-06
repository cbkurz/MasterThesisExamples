package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.MARTE.MARTEPackage;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GQAMPackage;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.impl.GQAMPackageImpl;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.impl.MARTE_AnalysisModelPackageImpl;
import org.eclipse.papyrus.MARTE.impl.MARTEFactoryImpl;
import org.eclipse.papyrus.MARTE.impl.MARTEPackageImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.nio.file.Paths;

public class UmlProfileMain {

    private static final ResourceSet resourceSet = new ResourceSetImpl();

    static {
        UMLResourcesUtil.init(resourceSet);
    }

    public static void main(String[] args) throws IOException {
        final Model model = createModel("ModelWithMarteImport");

        // Import of primitive Types (works fine)
        importProfile(model, URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));

        // Some MARTE init or else an exception is thrown. (only one is required, doesn't matter which one)
        MARTEPackageImpl.init();
        MARTEFactoryImpl.init();
        MARTE_AnalysisModelPackageImpl.init();
        GQAMPackageImpl.init();

        // Import of MARTE namespaces. (not working)
        // Reason: the "EcoreUtil.getObjectByType" returns null
        importProfile(model, URI.createURI(MARTEPackage.eNS_URI));
        importProfile(model, URI.createURI(MARTE_AnalysisModelPackageImpl.eNS_URI));
        importProfile(model, URI.createURI(GQAMPackage.eNS_URI));

        // Write model to file
        save(model);
    }

    protected static void importProfile(Model model, URI uri) {
        final Resource resource = resourceSet.getResource(uri, true);
        final org.eclipse.uml2.uml.Package umlLibrary = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
        model.createPackageImport(umlLibrary);
    }

    protected static Model createModel(String name) {
        Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);
        return model;
    }

    protected static void save(org.eclipse.uml2.uml.Package package_) throws IOException {
        URI outputURI = URI.createFileURI(Paths.get("model-output").toString())
                .appendSegment("ModelWithMarte")
                .appendFileExtension(UMLResource.FILE_EXTENSION);
        Resource resource = resourceSet.createResource(outputURI);
        resource.getContents().add(package_);
        resource.save(null);
    }

}
