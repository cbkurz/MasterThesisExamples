package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Node;

public class MarteSupport {

    public static final String GA_EXEC_HOST = "GaExecHost";

    /**
     *
     * @param umlMessage the UML Element on which the annotations shall be applied.
     *
     */
    static void setGaStepAsAnnotation(final Message umlMessage, final String execTime, final String repetitions) {
        UmlSupport.setAnnotationDetail(umlMessage, "GaStep", "execTime", execTime);
        UmlSupport.setAnnotationDetail(umlMessage, "GaStep", "rep", repetitions);
    }

    static void setGaWorkflow(final Lifeline lifeline, final String pattern) {
        UmlSupport.setAnnotationDetail(lifeline, "GaWorkload", "pattern", pattern);
    }

    static void setGaExecHost(final Node node) {
        node.getEAnnotations().stream()
                .filter(a -> a.getSource().equals(GA_EXEC_HOST))
                .findFirst()
                .orElseGet(() -> node.createEAnnotation(GA_EXEC_HOST));
    }
}
