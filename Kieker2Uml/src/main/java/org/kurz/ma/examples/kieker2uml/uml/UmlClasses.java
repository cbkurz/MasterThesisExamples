package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.Operation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setReferenceAnnotations;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.getMessageSort;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.setAnnotationDetail;

public class UmlClasses {

    final static private EClass OPERATION_E_CLASS = UMLFactory.eINSTANCE.createOperation().eClass();
    final static private EClass CLASS_E_CLASS = UMLFactory.eINSTANCE.createClass().eClass();
    static void addClasses(final Model model, final MessageTrace messageTrace) {
        requireNonNull(model, "model");
        requireNonNull(messageTrace, "messageTrace");

        final Package staticView = Kieker2UmlUtil.getPackagedElement(model, "staticView-classes");

        for (final AbstractMessage message : messageTrace.getSequenceAsVector()) {

            if (getMessageSort(message).equals(MessageSort.REPLY_LITERAL)) {
                // replys of messages do not need to be handled since both the receiver and the sender are added otherwise
                // It also does not add a required operation to the sender since it is a reply to a call and not the call itself.
                continue;
            }
            final org.eclipse.uml2.uml.Operation sender = getOperation(message.getSendingExecution().getOperation(), staticView);
            final org.eclipse.uml2.uml.Operation receiver = getOperation(message.getReceivingExecution().getOperation(), staticView);

            setReferenceAnnotations(sender, message.getSendingExecution());
            setReferenceAnnotations(receiver, message.getReceivingExecution());
            addDependency(sender, message.getReceivingExecution().getOperation());

            createAssociation(sender.getClass_(), receiver.getClass_());
        }
    }

    private static Association createAssociation(final Class from, final Class to) {
        requireNonNull(from, "from");
        requireNonNull(to, "to");

        final Optional<Association> first = from.getAssociations().stream()
                .filter(a -> a.getMemberEnds().size() == 2)
                .filter(a -> a.getMemberEnds().stream().anyMatch(me -> from.equals(me.getClass_())))
                .filter(a -> a.getMemberEnds().stream().anyMatch(me -> to.equals(me.getClass_())))
                .findFirst();

        if (first.isPresent()) {
            return first.get();
        }

        // Some of these values are chosen at random and have no meaning besides being there.
        // The following is the parameterlist and their names:
        //   boolean end1IsNavigable, AggregationKind end1Aggregation, String end1Name, int end1Lower, int end1Upper,
        //   Type end1Type, boolean end2IsNavigable, AggregationKind end2Aggregation, String end2Name, int end2Lower, int end2Upper
        return from.createAssociation(true, AggregationKind.NONE_LITERAL, null, 1, 1,
                to, true, AggregationKind.NONE_LITERAL, null, 1, 1);
    }

    private static void addDependency(final org.eclipse.uml2.uml.Operation from, final Operation operation) {
        setAnnotationDetail(from, "CallsToQualifiedNames", operation.toString(), null); // TODO: this does not jet add all dependencies but only one.
    }

    private static org.eclipse.uml2.uml.Operation getOperation(final Operation operation, final Package staticView) {

        // Traverse over all Classes, get the Operations and find the one with the qualified name of the kieker Operation.
        final Optional<org.eclipse.uml2.uml.Operation> umlOperation = staticView.getPackagedElements().stream()
                .filter(pe -> CLASS_E_CLASS.equals(pe.eClass()))
                .map(pe -> (org.eclipse.uml2.uml.Class) pe)
                .filter(c -> Kieker2UmlUtil.removeInstanceInformation(operation.getComponentType().getFullQualifiedName()).equals(c.getName()))
                .flatMap(c -> c.getOperations().stream())
                .filter(op -> operation.getSignature().toString().equals(op.getName()))
                .findFirst();

        if (umlOperation.isEmpty()) {
            final Class umlClass = createUmlClass(Kieker2UmlUtil.removeInstanceInformation(operation.getComponentType().getFullQualifiedName()), staticView);
            return umlClass.createOwnedOperation(operation.getSignature().toString(), null, null);
        }

        return umlOperation.get();
    }


    private static org.eclipse.uml2.uml.Class createUmlClass(final String clazzName, final Package staticView) {
        return staticView.getPackagedElements().stream()
                .filter(pe -> clazzName.equals(pe.getName()))
                .findFirst()
                .map(pe -> (org.eclipse.uml2.uml.Class) pe)
                .orElseGet(() -> (org.eclipse.uml2.uml.Class) staticView.createPackagedElement(clazzName, CLASS_E_CLASS));
    }
}
