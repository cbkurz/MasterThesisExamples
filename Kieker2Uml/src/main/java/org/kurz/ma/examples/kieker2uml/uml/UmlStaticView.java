package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.Operation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;

import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.kurz.ma.examples.kieker2uml.uml.UmlUtil.applyReferenceAnnotations;
import static org.kurz.ma.examples.kieker2uml.uml.UmlUtil.getMessageSort;
import static org.kurz.ma.examples.kieker2uml.uml.UmlUtil.setAnnotationDetail;

public class UmlStaticView {

    final static private EClass OPERATION_E_CLASS = UMLFactory.eINSTANCE.createOperation().eClass();
    final static private EClass CLASS_E_CLASS = UMLFactory.eINSTANCE.createClass().eClass();
    static void addStaticViewToModel(final Model model, final MessageTrace messageTrace) {
        requireNonNull(model, "model");
        requireNonNull(messageTrace, "messageTrace");

        final Package staticView = UmlUtil.getPackagedElement(model, "staticView");

        for (final AbstractMessage message : messageTrace.getSequenceAsVector()) {

            if (getMessageSort(message).equals(MessageSort.REPLY_LITERAL)) {
                // replys of messages do not need to be handled since both the receiver and the sender are added otherwise
                // It also does not add a required operation to the sender since it is a reply to a call and not the call itself.
                continue;
            }
            final org.eclipse.uml2.uml.Operation sender = getOperation(message.getReceivingExecution().getOperation(), staticView);
            final org.eclipse.uml2.uml.Operation receiver = getOperation(message.getReceivingExecution().getOperation(), staticView);

            applyReferenceAnnotations(sender, message.getSendingExecution());
            applyReferenceAnnotations(receiver, message.getReceivingExecution());
            addDependency(sender, message.getReceivingExecution().getOperation());
        }
    }

    private static void addDependency(final org.eclipse.uml2.uml.Operation from, final Operation operation) {
        setAnnotationDetail(from, "CallsToQualifiedNames", operation.toString(), null); // TODO: this does not jet add all dependencies but only one.
    }

    private static org.eclipse.uml2.uml.Operation getOperation(final Operation operation, final Package staticView) {

        // Traverse over all Classes, get the Operations and find the one with the qualified name of the kieker Operation.
        final Optional<org.eclipse.uml2.uml.Operation> umlOperation = staticView.getPackagedElements().stream()
                .filter(pe -> CLASS_E_CLASS.equals(pe.eClass()))
                .map(pe -> (org.eclipse.uml2.uml.Class) pe)
                .flatMap(clazz -> clazz.getOperations().stream())
                .filter(op -> operation.toString().equals(op.getName()))
                .findFirst();

        if (umlOperation.isEmpty()) {
            final Class umlClass = createUmlClass(operation.getComponentType().getFullQualifiedName(), staticView);
            return umlClass.createOwnedOperation(operation.toString(), null, null); // TODO: ParameterList
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
