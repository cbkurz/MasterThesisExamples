package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.MessageTrace;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.Message;
import org.eclipse.uml2.uml.Node;

import java.util.Arrays;
import java.util.Optional;

public class MarteSupport {

    public static final String GA_EXEC_HOST = "GaExecHost";
    public static final String GA_STEP_ANNOTATION_NAME = "GaStep";
    public static final String EXEC_TIME_ENTRIES_GA_STEP = "execTimeEntries";
    public static final String EXEC_TIME_GA_STEP = "execTime";
    public static final String REP_GA_STEP = "rep";

    /**
     * By Default the rep Attribute (repetition) is set to 1
     * @param umlMessage the UML Element on which the annotations shall be applied.
     * @param execTime the time of execution
     */
    static void setGaStep(final Element umlMessage, final long execTime) {
        final String execTimeString = Long.toString(execTime);
        Kieker2UmlUtil.setAnnotationDetail(umlMessage, GA_STEP_ANNOTATION_NAME, EXEC_TIME_ENTRIES_GA_STEP, execTimeString);
        Kieker2UmlUtil.setAnnotationDetail(umlMessage, GA_STEP_ANNOTATION_NAME, EXEC_TIME_GA_STEP, execTimeString);
        Kieker2UmlUtil.setAnnotationDetail(umlMessage, GA_STEP_ANNOTATION_NAME, REP_GA_STEP, "1");
    }

    /**
     * The repetition is determined by the amount of exeTimeEntries there are.
     * The execTime is determined by the mean value of the execTimeEntries.
     * @param element The Element to which the GaStep shall be applied.
     */
    static void updateGaStep(final Element element, Long execTime) {
        final Optional<EMap<String, String>> annotationDetailsOptional = Kieker2UmlUtil.getAnnotationDetailsMap(element, GA_STEP_ANNOTATION_NAME);
        if (annotationDetailsOptional.isEmpty()) {
            setGaStep(element, execTime);
            return;
        }

        final EMap<String, String> details = annotationDetailsOptional.get();
        final String currentExecTimeEntries = details.get(EXEC_TIME_ENTRIES_GA_STEP);
        final String execTimeString = Long.toString(execTime);
        final String execTimeCSV = currentExecTimeEntries + "," + execTimeString;

        final String[] execTimeSplit = execTimeCSV.split(","); // the length of this is the amounts of repetitions
        final Long sum = Arrays.stream(execTimeSplit)
                .map(Long::parseLong)
                .reduce(0L, Long::sum);

        final long execTimeMean = Long.divideUnsigned(sum, execTimeSplit.length);

        details.put(EXEC_TIME_ENTRIES_GA_STEP, execTimeCSV);
        details.put(EXEC_TIME_GA_STEP, Long.toString(execTimeMean));
        details.put(REP_GA_STEP, Integer.toString(execTimeSplit.length));
    }

    static void setGaWorkflow(final Lifeline lifeline, final String pattern) {
        Kieker2UmlUtil.setAnnotationDetail(lifeline, "GaWorkload", "pattern", pattern);
    }

    static void setGaExecHost(final Node node) {
        node.getEAnnotations().stream()
                .filter(a -> a.getSource().equals(GA_EXEC_HOST))
                .findFirst()
                .orElseGet(() -> node.createEAnnotation(GA_EXEC_HOST));
    }

    public static void applyPerformanceStereotypesToInteraction(final Interaction interaction, final MessageTrace messageTrace) {

    }

    private static void applyPerformanceAnnotations(final AbstractMessage message, final Lifeline senderLifeline, final Message umlMessage) {
        final long execTime = (message.getReceivingExecution().getTout() - message.getReceivingExecution().getTin());
        setGaStep(umlMessage, execTime);
        setGaWorkflow(senderLifeline, "closed:2"); // TODO: what exactly are those two values?
    }
}
