package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.emf.common.util.URI;

public class PackageNotPresentException extends RuntimeException {

    public PackageNotPresentException(URI uri) {
        super("Could not find the package with the uri: " + uri);
    }

}
