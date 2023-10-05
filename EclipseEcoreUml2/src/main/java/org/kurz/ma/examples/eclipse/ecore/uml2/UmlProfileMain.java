package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.kurz.ma.examples.eclipse.ecore.uml2.utils.UMLMarte;

import java.io.IOException;
import java.nio.file.Paths;

public class UmlProfileMain {

    private static final ResourceSet RESOURCE_SET = new ResourceSetImpl();

    static {
        UMLResourcesUtil.init(RESOURCE_SET);
        UMLMarte.initMARTE(RESOURCE_SET);
    }

    public static void main(String[] args) throws IOException {
        final Model model = createModel("ModelWithMarteImport");

        // Create Model Items
        final Profile profile = importProfile(model, UMLMarte.getGqamUri());
        final EClass deploymentEClass = UMLFactory.eINSTANCE.createDeployment().eClass();
        final Node node = UMLFactory.eINSTANCE.createNode();
        final EClass nodeEClass = node.eClass();

        // Import into model
        final Deployment deployment = (Deployment) model.createPackagedElement("myDeployment", deploymentEClass);
        final Node myNode = (Node) model.createPackagedElement("myNode", nodeEClass);
        deployment.createDependency(myNode);

        myNode.createOwnedAttribute("myAttribute", UMLFactory.eINSTANCE.createPrimitiveType());

        // Apply Stereotype - does not work and fails (?) silently
        // Meaning of "does not work" is, that after saving there is no stereotype attached to the node.
        final Stereotype gaExecHost = getGaExecHost(profile);
        final EObject eObject = myNode.applyStereotype(gaExecHost);

        save(model);
    }

    private static Stereotype getGaExecHost(final Profile profile) {
        return profile.allApplicableStereotypes().stream().filter(s -> "GaExecHost".equals(s.getName())).findFirst().get();
    }

    protected static Profile importProfile(Model model, URI uri) {
        final Resource resource = RESOURCE_SET.getResource(uri, true);
        final org.eclipse.uml2.uml.Package umlLibrary = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
        final PackageImport packageImport = model.createPackageImport(umlLibrary);
        final Profile profile = (Profile) packageImport.getImportedPackage();
        model.applyProfile(profile);
        final Profile gqamProfile = (Profile) profile.getPackagedElements().get(3).allOwnedElements().get(1); // this is required since
        model.applyProfile(gqamProfile);
        return profile;
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
        Resource resource = RESOURCE_SET.createResource(outputURI);
        resource.getContents().add(package_);
        resource.save(null);
    }

    private static Model loadModel(final String modelPath) {
        final Resource model = RESOURCE_SET.getResource(URI.createURI(modelPath), true);
        if (model == null) {
            throw new RuntimeException("Could not load Model: " + modelPath);
        }

        return (Model) model.getContents().get(0);
    }

}
