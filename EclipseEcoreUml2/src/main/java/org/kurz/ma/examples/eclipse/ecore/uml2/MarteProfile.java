package org.kurz.ma.examples.eclipse.ecore.uml2;

import org.eclipse.emf.common.util.WrappedException;

public class MarteProfile {

//    private static final Profile MARTE;

    static {
        org.eclipse.uml2.uml.Package package_ = null;

        try {
            // Load the requested resource
/*
            Resource resource = RESOURCE_SET.getResource(uri, true);

            // Get the first (should be only) package from it
            package_ = (org.eclipse.uml2.uml.Package) EcoreUtil
                    .getObjectByType(resource.getContents(),
                            UMLPackage.Literals.PACKAGE);
*/
        } catch (WrappedException we) {
            System.exit(1);
        }
    }

    public MarteProfile() {

    }
}
