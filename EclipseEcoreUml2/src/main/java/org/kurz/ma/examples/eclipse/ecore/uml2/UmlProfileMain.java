package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GQAMFactory;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GaExecHost;
import org.eclipse.uml2.uml.Device;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageImport;
import org.eclipse.uml2.uml.Profile;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class UmlProfileMain {

    private static final ResourceSet RESOURCE_SET = new ResourceSetImpl();

    static {
        UMLResourcesUtil.init(RESOURCE_SET);
    }

    public static void main(String[] args) throws IOException {
        final Model model = createModel("ModelWithMarteImport");

        final URI marteUri = URI.createFileURI(Paths.get("EclipseEcoreUml2").resolve("MARTE").toString()).appendSegment("MARTE").appendFileExtension(UMLResource.PROFILE_FILE_EXTENSION);

        final PackageImport packageImport = importProfile(model, marteUri);

        final Profile profile = (Profile) packageImport.getImportedPackage();
        createNodes(model, profile);

        final Device device = UMLFactory.eINSTANCE.createDevice();
        final GaExecHost stereotype = (GaExecHost) device.applyStereotype(getGaExecHost());


//        final GaExecHost gaExecHost = (GaExecHost) model.applyStereotype(getGaExecHost());
//        gaExecHost.setClockOvh("ck-clock");


//        model.createOwnedStereotype("")

        save(model);
    }

    private static void createNodes(final Model model, final Profile profile) {
        final EClass interactionEClass = UMLFactory.eINSTANCE.createInteraction().eClass();
        final Interaction interaction = (Interaction) model.createPackagedElement("MyInteraction", interactionEClass);
        final Lifeline lifeline = interaction.createLifeline("MyLifeline");
        final List<Stereotype> list = profile.allApplicableStereotypes().stream().filter(interaction::isStereotypeApplicable).toList();
        System.out.println("Applicable Stereotypes: " + list);
    }

    private static Stereotype getGaExecHost() {
        return (Stereotype) GQAMFactory.eINSTANCE.getGQAMPackage().getGaExecHost();
    }

    protected static PackageImport importProfile(Model model, URI uri) {
        final Resource resource = RESOURCE_SET.getResource(uri, true);
        final org.eclipse.uml2.uml.Package umlLibrary = (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
        final PackageImport packageImport = model.createPackageImport(umlLibrary);
        return packageImport;
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

    protected static org.eclipse.uml2.uml.Package load(URI uri) {
        org.eclipse.uml2.uml.Package package_ = null;

        try {
            // Load the requested resource
            Resource resource = RESOURCE_SET.getResource(uri, true);

            // Get the first (should be only) package from it
            package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
                    .getObjectByType(resource.getContents(),
                            UMLPackage.Literals.PACKAGE);
        } catch (WrappedException we) {
            System.exit(1);
        }

        return package_;
    }


}
