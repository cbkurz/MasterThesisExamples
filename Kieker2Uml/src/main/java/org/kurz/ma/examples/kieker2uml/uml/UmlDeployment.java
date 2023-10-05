package org.kurz.ma.examples.kieker2uml.uml;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Artifact;
import org.eclipse.uml2.uml.Component;
import org.eclipse.uml2.uml.DeployedArtifact;
import org.eclipse.uml2.uml.Deployment;
import org.eclipse.uml2.uml.Manifestation;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Node;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;

import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class UmlDeployment {

    public static final EClass NODE_E_CLASS = UMLFactory.eINSTANCE.createNode().eClass();
    public static final EClass COMPONENT_E_CLASS = UMLFactory.eINSTANCE.createComponent().eClass();
    public static final EClass PACKAGE_E_CLASS = UMLFactory.eINSTANCE.createPackage().eClass();
    public static final EClass ARTIFACT_E_CLASS = UMLFactory.eINSTANCE.createArtifact().eClass();

    static void addDeploymentToModel(final Model model, final String componentName, final String artifactName) {
        requireNonNull(componentName, "componentName");
        requireNonNull(artifactName, "artifactName");

        final Package staticView = getPackagedElement(model, "staticView");
        final Component component = createComponent(componentName, staticView);

        final Package deploymentView = getPackagedElement(model, "deploymentView");
        final Node node = createNode(deploymentView, componentName);
        final Artifact artifact = createArtifact(deploymentView, artifactName);

        createManifestation(artifact, component, componentName);
        final Deployment deployment = createDeployment(node, componentName);
        addArtifact(deployment, artifact);

        MarteSupport.setGaExecHost(node);
    }

    private static void createManifestation(final Artifact artifact, final Component component, final String componentName) {
        final String manifestationName = componentName + "-manifestation";
        final Manifestation manifestation = artifact.getManifestation(manifestationName, component);
        if (isNull(manifestation)) {
            artifact.createManifestation(manifestationName, component);
        }
    }

    private static Component createComponent(final String componentName, final Package staticView) {
        return staticView.getPackagedElements().stream()
                .filter(pe -> componentName.equals(pe.getName()))
                .findFirst()
                .map(p -> (Component) p)
                .orElseGet(() -> (Component) staticView.createPackagedElement(componentName, COMPONENT_E_CLASS));
    }

    private static Node createNode(final Package deploymentView, final String componentName) {
        final String nodeName = componentName + "-environment";
        return deploymentView.getPackagedElements().stream()
                .filter(np -> nodeName.equals(np.getName())).findFirst()
                .map(aPackage -> (Node) aPackage)
                .orElseGet(() -> (Node) deploymentView.createPackagedElement(nodeName, NODE_E_CLASS));
    }

    private static Artifact createArtifact(final Package deploymentView, final String artifactName) {
        return deploymentView.getPackagedElements().stream()
                .filter(np -> artifactName.equals(np.getName()))
                .findFirst()
                .map(aPackage -> (Artifact) aPackage)
                .orElseGet(() -> (Artifact) deploymentView.createPackagedElement(artifactName, ARTIFACT_E_CLASS));
    }

    private static Deployment createDeployment(final Node node, final String componentName) {
        final String deploymentName = componentName + "-deployment";
        final Deployment deployment = node.getDeployment(deploymentName);
        if (isNull(deployment)) {
            return node.createDeployment(deploymentName);
        }
        return deployment;
    }

    private static void addArtifact(final Deployment deployment, final Artifact  artifact) {
        final Optional<DeployedArtifact> first = deployment.getDeployedArtifacts().stream()
                .filter(a -> artifact.getName().equals(a.getName())).findFirst();
        if (first.isEmpty()) {
            deployment.getDeployedArtifacts().add(artifact);
        }
    }


    private static Package getPackagedElement(final Model model, final String packageName) {
        return (Package) model.getPackagedElements().stream().filter(p -> p.getName().equals(packageName)).findFirst().orElseGet(() -> model.createPackagedElement(packageName, PACKAGE_E_CLASS));
    }

}
