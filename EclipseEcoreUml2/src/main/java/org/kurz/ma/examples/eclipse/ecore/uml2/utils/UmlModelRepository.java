package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class UmlModelRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(UmlModelRepository.class);

    private final Map<String, ModelManagement> models = new HashMap<>();

    private final ResourceSet resourceSet = new ResourceSetImpl();

    public UmlModelRepository() {
        UMLResourcesUtil.init(resourceSet);
    }


    public void register(final String identifier, final Model model) {
        requireNonNull(identifier, "The identifier for the model must not be null.");
        requireNonNull(model, "A model has to be provided when trying to register one.");
        LOGGER.debug("Model Identifier: " + identifier);
        LOGGER.debug("Registering Model: " + model.getQualifiedName());

        models.put(identifier, new ModelManagementImpl(this, model, identifier));

        LOGGER.debug("Success fully registered model with identifier: " + identifier);
    }

    public ModelManagement createModel(final String identifier) {
        requireNonNull(identifier, "The identifier for the model must not be null.");
        final ModelManagementImpl modelManagement = new ModelManagementImpl(this, identifier);
        models.put(identifier, modelManagement);
        return modelManagement;
    }

    public Optional<ModelManagement> getModel(final String identifier) {
        return Optional.ofNullable(models.get(identifier));
    }

    /**
     * If the profile is not jet registered it is loaded on demand.
     * The URI for the Primitive profile can be obtained with: UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI
     * @param uri - the uri under which the profile is registered
     * @return The Resource containing the profile.
     */
    public Optional<Resource> getResource(final URI uri) {
        try {
            return Optional.ofNullable(resourceSet.getResource(uri, true));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    protected void save(org.eclipse.uml2.uml.Package package_, URI uri) {
        Resource resource = resourceSet.getResources().stream()
                .filter(r -> uri.equals(r.getURI()))
                .findFirst()
                .orElseGet(() -> resourceSet.createResource(uri));
        resource.getContents().add(package_);
        try {
            resource.save(null); // no save options needed
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        LOGGER.info("Saved to file: " + uri);
    }
}
