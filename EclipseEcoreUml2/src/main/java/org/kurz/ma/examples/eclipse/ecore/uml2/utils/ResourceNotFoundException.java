package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.emf.common.util.URI;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(URI resourceUri) {
        super("The resource could not be found, uri: " + resourceUri.toString());
    }

}
