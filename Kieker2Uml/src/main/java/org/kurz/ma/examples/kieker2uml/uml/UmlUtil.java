package org.kurz.ma.examples.kieker2uml.uml;

import kieker.common.util.signature.Signature;
import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.Execution;
import kieker.model.system.model.SynchronousCallMessage;
import kieker.model.system.model.SynchronousReplyMessage;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.MessageSort;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class UmlUtil {
    public static final EClass PACKAGE_E_CLASS = UMLFactory.eINSTANCE.createPackage().eClass();
    public static final String REFERENCE_ANNOTATION_NAME = "Reference";

    static Package getPackagedElement(final Model model, final String packageName) {
        return (Package) model.getPackagedElements().stream()
                .filter(p -> p.getName().equals(packageName))
                .findFirst()
                .orElseGet(() -> model.createPackagedElement(packageName, PACKAGE_E_CLASS));
    }

    static void setAnnotationDetail(final Element element, final String annotationName, final String key, final String value) {
        final EAnnotation eAnnotation = element.getEAnnotations().stream()
                .filter(a -> a.getSource().equals(annotationName))
                .findFirst()
                .orElseGet(() -> element.createEAnnotation(annotationName));

        final EMap<String, String> details = eAnnotation.getDetails();
        if (details.containsKey(key)) {
            details.removeKey(key);
            details.put(key, value);
        } else {
            details.put(key, value);
        }
    }

    public static Path saveModel(Model model, Path targetFile) {
        final ResourceSet resourceSet = new ResourceSetImpl();

        UMLResourcesUtil.init(resourceSet);

        Resource resource = resourceSet.createResource(URI.createURI(targetFile.toString()));
        resource.getContents().add(model);

        // And save
        try {
            resource.save(null); // no save options needed
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return targetFile;
    }

    public static Model loadModel(Path pathToModel) {

        // unclear why this is necessary,
        // however if this isn't initialized here again,
        // the program fails since the model will not be loaded when it is created by the InputModelValidator.class.
        URI typesUri = URI.createFileURI(pathToModel.toString());
        ResourceSet set = new ResourceSetImpl();

        set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
        set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
        set.createResource(typesUri);
        Resource r = set.getResource(typesUri, true);

        return requireNonNull((Model) EcoreUtil.getObjectByType(r.getContents(), UMLPackage.Literals.MODEL));
    }

    public static Model createModel(final String name) {
        final Model model = UMLFactory.eINSTANCE.createModel();
        model.setName(name);
        return model;
    }

    static String getMessageLabel(final Execution execution) {
        final Signature sig = execution.getOperation().getSignature();
        final String params = String.join(", ", sig.getParamTypeList());
        return sig.getName() + '(' + params + ')';
    }

    /**
     * {@link MessageSort} is an enumeration of different kinds of messages.
     * This enumeration determines if it is a call or a reply.
     * This method only expects there to be 2 types {MessageSort.SYNCH_CALL_LITERAL} or {MessageSort.REPLY_LITERAL}
     * {@link MessageSort}
     * @param message the kieker trace message, two types are considered {@link SynchronousCallMessage} and {@link SynchronousReplyMessage} if neither are matched an exception is thrown.
     * @return MessageSort Literal
     */
    public static MessageSort getMessageSort(final AbstractMessage message) {
        if (message instanceof SynchronousCallMessage) {
            return MessageSort.SYNCH_CALL_LITERAL;
        }
        if (message instanceof SynchronousReplyMessage) {
            return MessageSort.REPLY_LITERAL;
        }
        throw new RuntimeException("Unexpected message type of: " + message.getClass());

    }

    static void applyReferenceAnnotations(final Element element, final Execution execution) {
        setAnnotationDetail(element, REFERENCE_ANNOTATION_NAME, "package", execution.getOperation().getComponentType().getPackageName());
        setAnnotationDetail(element, REFERENCE_ANNOTATION_NAME, "class", execution.getOperation().getComponentType().getTypeName());
        setAnnotationDetail(element, REFERENCE_ANNOTATION_NAME, "fullQualifiedName", execution.getOperation().getComponentType().getFullQualifiedName());
        setAnnotationDetail(element, REFERENCE_ANNOTATION_NAME, "fullQualifiedNameSignature", execution.getOperation().toString());
        setAnnotationDetail(element, REFERENCE_ANNOTATION_NAME, "signature", execution.getOperation().getSignature().toString());
    }

    static Optional<String> getReference(final Element element, final String key) {
        return element.getEAnnotations().stream()
                .filter(a -> REFERENCE_ANNOTATION_NAME.equals(a.getSource()))
                .findFirst()
                .map(a -> a.getDetails().get(key));
    }

    static String removeInstanceInformation(final String name) {
        return name.replaceAll("\\$[0-9]*", "");
    }
}
