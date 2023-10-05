package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.BehaviorExecutionSpecification;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.MessageOccurrenceSpecification;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.UMLFactory;
import org.kurz.ma.examples.eclipse.ecore.uml2.utils.ModelManagement;
import org.kurz.ma.examples.eclipse.ecore.uml2.utils.UmlModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

public class UmlMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmlMain.class);


    public static final EClass MESSAGE_OCCURRENCE_E_CLASS = UMLFactory.eINSTANCE.createMessageOccurrenceSpecification().eClass();
    public static final EClass BEHAVIOUR_EXECUTION_E_CLASS = UMLFactory.eINSTANCE.createBehaviorExecutionSpecification().eClass();

    public static void main(String[] args) throws IOException {
        LOGGER.info("--- Starting ---");

        final UmlModelRepository repository = new UmlModelRepository();

        final ModelManagement model = repository.createModel("ModelWithInteractions");

        model.importPrimitivesProfile();

        final Interaction interaction = model.createInteraction("Interaction");
        final Node node = model.createNode("myNode");

        final Lifeline myLifeline = interaction.createLifeline("myLifeline");
        final Message myMessage = interaction.getMessage("myMessage");
        final MessageOccurrenceSpecification myMOS = (MessageOccurrenceSpecification) interaction.createFragment("myMOS", MESSAGE_OCCURRENCE_E_CLASS);
        final BehaviorExecutionSpecification myBES = (BehaviorExecutionSpecification) interaction.createFragment("myBES", BEHAVIOUR_EXECUTION_E_CLASS);

        final EAnnotation myAnnotation = myBES.createEAnnotation("myAnnotation");
        myAnnotation.getDetails().put("ref", "myRef");

        model.save(Paths.get("model-output"));
        LOGGER.info("--- Finnished ---");
    }


}
