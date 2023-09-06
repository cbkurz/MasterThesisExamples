package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.papyrus.MARTE.MARTEPackage;
import org.eclipse.papyrus.MARTE.impl.MARTEPackageImpl;
import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public class ModelManagementImpl implements ModelManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelManagementImpl.class);

    final private Model model;
    final private String name;
    private final UmlModelRepository repository;

    public ModelManagementImpl(final UmlModelRepository umlModelRepository, final String name) {
        this.repository = umlModelRepository;
        this.model = UMLFactory.eINSTANCE.createModel();
        this.name = name;
        this.model.setName(name);
    }

    public ModelManagementImpl(final UmlModelRepository umlModelRepository, final Model model, final String name) {
        this.repository = umlModelRepository;
        this.model = model;
        this.name = name;
    }

    @Override
    public Interaction createInteraction(final String name) {
        final Interaction interaction = UMLFactory.eINSTANCE.createInteraction();
        return (Interaction) model.createPackagedElement(name, interaction.eClass());
    }

    @Override
    public void importPrimitivesProfile() {
        final org.eclipse.uml2.uml.Package umlLibrary = loadUmlPackage(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI));
        model.createPackageImport(umlLibrary);
    }

    @Override
    public void importMarteProfile() {
        MARTEPackageImpl.init();
        final URI uri = URI.createURI(MARTEPackage.eNS_URI);
        final org.eclipse.uml2.uml.Package umlLibrary = Optional.ofNullable(loadUmlPackage(uri)).orElseThrow(() -> new PackageNotPresentException(uri));
        model.createPackageImport(umlLibrary);
    }

    @Override
    public void save(final Path folder) {
        save(folder, name);
    }

    @Override
    public void save(final Path folder, final String name) {
        repository.save(
                model,
                URI.createFileURI(folder.toString())
                .appendSegment(name)
                .appendFileExtension(UMLResource.FILE_EXTENSION)
        );
    }


    protected org.eclipse.uml2.uml.Package loadUmlPackage(URI uri) {
        final Resource resource = this.repository.getResource(uri).orElseThrow(() -> new ResourceNotFoundException(uri));
        return (org.eclipse.uml2.uml.Package) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.PACKAGE);
    }


    public String getName() {
        return name;
    }
}
