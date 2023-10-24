package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.MessageTrace;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class UmlComponents {

    private static List<MessageTrace> traces = new ArrayList<>();


    static void addComponents(final Model model, final MessageTrace messageTrace) {
        requireNonNull(model, "model");
        requireNonNull(messageTrace, "messageTrace");

        traces.add(messageTrace);

        final Package staticView = Kieker2UmlUtil.getPackagedElement(model, "staticView-components");
    }

}
