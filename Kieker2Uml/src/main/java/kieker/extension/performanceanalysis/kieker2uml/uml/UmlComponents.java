package kieker.extension.performanceanalysis.kieker2uml.uml;

import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.MessageTrace;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Usage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class UmlComponents {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmlComponents.class);
    private static List<MessageTrace> traces = new ArrayList<>();


    static void addComponentsAndDeployment(final Model model, final MessageTrace messageTrace) {
        requireNonNull(model, "model");
        requireNonNull(messageTrace, "messageTrace");

        traces.add(messageTrace);

        final Package staticView = Kieker2UmlUtil.getPackagedElement(model, "staticView-components");
        final Package deploymentView = Kieker2UmlUtil.getPackagedElement(model, "deploymentView");

        for (final AbstractMessage message : messageTrace.getSequenceAsVector()) {

            if (Kieker2UmlUtil.getMessageSort(message).equals(MessageSort.REPLY_LITERAL)) {
                continue;
            }

            // sender
            // names
            final String senderInterfaceName = message.getSendingExecution().getOperation().getSignature().toString();
            final String senderComponentName = message.getSendingExecution().getAllocationComponent().getAssemblyComponent().getIdentifier();
            final String senderArtifactName = message.getSendingExecution().getAllocationComponent().getIdentifier();
            final String senderNodeName = message.getSendingExecution().getAllocationComponent().getExecutionContainer().getIdentifier();

            // uml elements
            final Component senderComponent = getComponent(staticView, senderComponentName);
            final Interface senderInterface = getInterface(staticView, senderInterfaceName);
            final Node senderNode = getNode(deploymentView, senderNodeName);
            final Artifact senderArtifact = getArtifact(deploymentView, senderArtifactName);

            // connection
            doConnection(model, senderInterface, senderComponent, senderArtifact, senderNode);

            // receiver
            // names
            final String receiverInterfaceName = message.getReceivingExecution().getOperation().getSignature().toString();
            final String receiverComponentName = message.getReceivingExecution().getAllocationComponent().getAssemblyComponent().getIdentifier();
            final String receiverArtifactName = message.getReceivingExecution().getAllocationComponent().getIdentifier();
            final String receiverNodeName = message.getReceivingExecution().getAllocationComponent().getExecutionContainer().getIdentifier();

            // uml elements
            final Component receiverComponent = getComponent(staticView, receiverComponentName);
            final Interface receiverInterface = getInterface(staticView, receiverInterfaceName);
            final Node receiverNode = getNode(deploymentView, receiverNodeName);
            final Artifact receiverArtifact = getArtifact(deploymentView, receiverArtifactName);

            // connection
            doConnection(model, receiverInterface, receiverComponent, receiverArtifact, receiverNode);

            // sender uses receiver
            getUsage(staticView, senderInterface, receiverInterface);

        }
    }

    private static Node getNode(final Package deploymentView, final String nodeName) {
        return (Node) deploymentView.getPackagedElement(nodeName, false, UMLPackage.Literals.NODE, true);
    }

    private static Artifact getArtifact(final Package deploymentView, final String artifactName) {
        return (Artifact) deploymentView.getPackagedElement(artifactName, false, UMLPackage.Literals.ARTIFACT, true);
    }

    private static void doConnection(final Model model, final Interface anInterface, final Component component, final Artifact artifact, final Node node) {
        component.getOwnedOperation(anInterface.getName(), null, null, false, true);
        final Optional<Interface> first = component.allRealizedInterfaces().stream().filter(i -> i.equals(anInterface)).findFirst();
        if (first.isEmpty()) {
            component.createInterfaceRealization("Realization-" + anInterface.getName(), anInterface);
        }
        final List<Lifeline> lifelines = model.allOwnedElements().stream()
                .filter(e -> UMLPackage.Literals.LIFELINE.equals(e.eClass()))
                .map(e -> (Lifeline) e)
                .filter(l -> l.getName().equals(component.getName()))
                .filter(l -> !UmlUseCases.KIEKER_ENTRY_NAME.equals(l.getName()))
                .toList();

        lifelines.stream()
                .map(Lifeline::getInteraction)
                .distinct()
                .forEach(i -> i.getInterfaceRealization("Realization-" + anInterface.getName(), anInterface, false, true));

        lifelines.forEach(l -> l.setRepresents(l.getInteraction().getOwnedParameter("Representation-" + component.getName(), component, false, true)));

        artifact.getManifestation("ArtifactManifestation-" + component.getName(), component, false, true);
        final Deployment deployment = node.getDeployment("NodeDeployment-" + node.getName(), false, true);
        if (!deployment.getDeployedArtifacts().contains(artifact)) {
            deployment.getDeployedArtifacts().add(artifact);
        }
    }

    private static Component getComponent(final Package staticView, final String execution) {
        return (Component) staticView.getPackagedElement(execution, false, UMLPackage.Literals.COMPONENT, true);
    }

    private static Usage getUsage(final Package staticView, final Interface sender, final Interface receiver) {
        return staticView.getPackagedElements().stream()
                .filter(pe -> pe instanceof Usage)
                .map(pe -> (Usage) pe)
                .filter(u -> nonNull(u.getClient(sender.getName())))
                .filter(u -> nonNull(u.getSupplier(receiver.getName())))
                .findFirst()
                .orElseGet(() -> sender.createUsage(receiver));
    }

    private static Interface getInterface(final Package staticView, final String interfaceName) {
        return (Interface) staticView.getPackagedElement(interfaceName, false, UMLPackage.Literals.INTERFACE, true);
    }


}
