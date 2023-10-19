package org.kurz.ma.examples.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.ComponentType;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.Operation;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.kurz.ma.examples.kieker2uml.filter.Util.getMessageSort;

public class UmlDeployment2 {

    static void addStaticViewToModel(final Model model, final MessageTrace messageTrace) {
        requireNonNull(model, "model");
        requireNonNull(messageTrace, "messageTrace");

        final Package staticView = UmlUtil.getPackagedElement(model, "staticView");

        final Set<OperationNode> operationNodeSet = new HashSet<>();
        for (final AbstractMessage message : messageTrace.getSequenceAsVector()) {

            if (getMessageSort(message).equals(MessageSort.REPLY_LITERAL)) {
                // replys of messages do not need to be handled since both the receiver and the sender are added otherwise
                // It also does not add a required operation to the sender since it is a reply to a call and not the call itself.
                continue;
            }

            final OperationNode sender = getOperation(message.getSendingExecution().getOperation(), operationNodeSet);
            final OperationNode receiver = getOperation(message.getReceivingExecution().getOperation(), operationNodeSet);

            operationNodeSet.add(sender);
            operationNodeSet.add(receiver);
            sender.addRequiredOperation(receiver);
        }
        System.out.println(operationNodeSet);
    }

    private static OperationNode getOperation(final Operation op, final Set<OperationNode> operationNodeSet) {
        final OperationNode operationNode = new OperationNode(op);
        return operationNodeSet.stream()
                .filter(operationNode::equals) // the equals method on the OperationNode class checks for the full qualified name equality.
                .findFirst()
                .orElse(operationNode);
    }


    private static org.eclipse.uml2.uml.Component getModelComponent(final Model model, final ComponentType component) {
        return null;
    }

    private static ComponentType getComponent(final AbstractMessage message) {
        return message.getSendingExecution().getOperation().getComponentType();
    }


    static class OperationNode {
        final Operation operation;

        final Set<OperationNode> requiredOperationNodes = new HashSet<>();

        OperationNode(final Operation operation) {
            this.operation = operation;
        }

        public void addRequiredOperation(final OperationNode operationNode) {
            requiredOperationNodes.add(operationNode);
        }


        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final OperationNode that = (OperationNode) o;
            return Objects.equals(operation.toString(), that.operation.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(operation);
        }
    }
}
