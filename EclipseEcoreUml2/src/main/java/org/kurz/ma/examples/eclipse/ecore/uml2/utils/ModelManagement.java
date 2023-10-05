package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.uml2.uml.Interaction;
import org.eclipse.uml2.uml.Node;

import java.nio.file.Path;

public interface ModelManagement {

    Interaction createInteraction(String name);

    Node createNode(String name);

    void importPrimitivesProfile();
    void importMarteProfile();

    void save(Path folder);
    void save(Path folder, String name);

}
