package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;
import org.kurz.ma.examples.eclipse.ecore.uml2.utils.ModelManagement;
import org.kurz.ma.examples.eclipse.ecore.uml2.utils.UmlModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class UmlMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmlMain.class);



    public static void main(String[] args) throws IOException {
        LOGGER.info("--- Starting ---");

        final UmlModelRepository repository = new UmlModelRepository();

        final ModelManagement model = repository.createModel("ModelWithInteractions");

        model.importPrimitivesProfile();
        model.importMarteProfile();

        final Interaction interaction = UMLFactory.eINSTANCE.createInteraction();

        final Lifeline myLifeline = interaction.createLifeline("myLifeline");
        myLifeline.setVisibility(VisibilityKind.PUBLIC_LITERAL);
        if (model.equals(myLifeline.getModel())) {
            LOGGER.info("Lifeline is in model");
        }
        if (Objects.nonNull(interaction.getLifelines()) && !interaction.getLifelines().isEmpty()) {
            LOGGER.info("A lifeline has been registered.");
            if (interaction.getLifelines().get(0).equals(myLifeline)) {
                LOGGER.info("The lifeline registered is also the one created.");
            }
        }

        model.save(Paths.get("model-output"));
        LOGGER.info("--- Finnished ---");
    }

    private static PrimitiveType getIntPrimitive(final Model model) {
        return (PrimitiveType) model.getOwnedType("Integer");
    }

    protected static Property createAttribute(org.eclipse.uml2.uml.Class clazz, String name, Type type, int lowerBound, int upperBound) {

        return clazz.createOwnedAttribute(name, type,
                lowerBound, upperBound);
    }


}
