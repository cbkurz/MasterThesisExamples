package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.uml2.uml.Model;

import java.nio.file.Paths;

public class Kieker2UmlModel {

    public static void main(String[] args) {
        Model m = UmlUtil.loadModel(Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams.uml"));
        System.out.println(m.getName());
        UmlUtil.saveModel(m, Paths.get("Kieker2Uml/input-data/uml/SequenceDiagrams2.uml"));
    }

    public static void addInteractionToModel(final Model model, final MessageTrace messageTrace) {
        UmlInteractions.addInteractionToModel(model, messageTrace);
    }

    public static void addInteractionToUseCase(final Model model, final MessageTrace messageTrace, final String useCaseName) {
        UmlUseCases.addUseCase(model, messageTrace, useCaseName);
    }

    public static void addStaticViewToModel(final Model model, final MessageTrace trace) {
        UmlClasses.addClasses(model, trace);
    }

}
