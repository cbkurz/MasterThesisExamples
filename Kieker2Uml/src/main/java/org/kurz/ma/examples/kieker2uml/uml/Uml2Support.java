package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.kurz.ma.examples.kieker2uml.model.Lifeline;
import org.kurz.ma.examples.kieker2uml.model.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static void addInteractionToModel(final Model model, final String interactionName, final Set<Lifeline> lifelines) {
        final EClass eClass = UMLFactory.eINSTANCE.createInteraction().eClass();
        final Interaction interaction = (Interaction) model.createPackagedElement(interactionName, eClass);

        final List<org.eclipse.uml2.uml.Lifeline> umlLifelines = addLifelines(lifelines, interaction);

        lifelines.stream().flatMap(l -> l.getMessagesOutgoing().stream()).toList().forEach(m -> createMessage(interaction, umlLifelines, m));
        lifelines.stream().flatMap(l -> l.getMessagesIncoming().stream()).toList().forEach(m -> createMessage(interaction, umlLifelines, m));
    }

    private static List<org.eclipse.uml2.uml.Lifeline> addLifelines(final Set<Lifeline> lifelines, final Interaction interaction) {
        final List<org.eclipse.uml2.uml.Lifeline> umlLifelines = new ArrayList<>();
        lifelines.forEach(l -> {
            final org.eclipse.uml2.uml.Lifeline lifeline = interaction.createLifeline(l.getLabel());
            lifeline.setName(l.getLabel()); // TODO: redundant?
            umlLifelines.add(lifeline);
        });
        return umlLifelines;
    }

    private static void createMessage(final Interaction interaction, final List<org.eclipse.uml2.uml.Lifeline> umlLifelines, final Message m) {
        final org.eclipse.uml2.uml.Message umlMessage = interaction.createMessage(m.getLabel());
        final org.eclipse.uml2.uml.Lifeline from = getLifeline(umlLifelines, m.getFrom());
        final org.eclipse.uml2.uml.Lifeline to = getLifeline(umlLifelines, m.getTo());

        createMessageOccurance(interaction, umlMessage, from, m.getLabel() + "SendEvent");
        createMessageOccurance(interaction, umlMessage, to, m.getLabel() + "ReceiveEvent");
    }

    private static void createMessageOccurance(final Interaction interaction, final org.eclipse.uml2.uml.Message umlMessage, final org.eclipse.uml2.uml.Lifeline lifeline, final String label) {
        final MessageOccurrenceSpecification fragment = (MessageOccurrenceSpecification) interaction.createFragment(label, UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass());

        fragment.getCovereds().add(lifeline);
        fragment.setMessage(umlMessage);
    }

    private static org.eclipse.uml2.uml.Lifeline getLifeline(final List<org.eclipse.uml2.uml.Lifeline> umlLifelines, final Lifeline m) {
        return umlLifelines.stream().filter(l -> l.getLabel().equals(m.getLabel())).findFirst().orElseThrow();
    }

}
