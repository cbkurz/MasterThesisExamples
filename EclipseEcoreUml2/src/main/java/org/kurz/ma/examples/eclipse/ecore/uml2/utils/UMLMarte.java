package org.kurz.ma.examples.eclipse.ecore.uml2.utils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.papyrus.MARTE.MARTE_AnalysisModel.GQAM.GQAMPackage;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.Alloc.AllocPackage;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.CoreElements.CoreElementsPackage;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.GRM.GRMPackage;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.NFPs.NFPsPackage;
import org.eclipse.papyrus.MARTE.MARTE_Foundations.Time.TimePackage;
import org.eclipse.uml2.uml.UMLPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class UMLMarte {
	
	private static final Logger LOGGER = Logger.getLogger(UMLMarte.class.getName());
	               
	// MARTE       
	private static final String MARTE_BASE_PATHMAP = "pathmap://Papyrus_PROFILES/";
	private static final String MARTE_PROFILE = "MARTE.profile.uml#";
	private static final String MARTE_NFP_FRAGMENT = "_U_GAoAPMEdyuUt-4qHuVvQ";
	private static final String MARTE_TIME_FRAGMENT = "_WStkoAPMEdyuUt-4qHuVvQ";
	private static final String MARTE_GRM_FRAGMENT = "_XVWGUAPMEdyuUt-4qHuVv";
	private static final String MARTE_ALLOC_FRAGMENT = "_ar8OsAPMEdyuUt-4qHuVvQ";
	private static final String MARTE_CORE_ELEMENTS_FRAGMENT = "_-wEewECLEd6UTJZnztgOLw";
	private static final String MARTE_GQAM_FRAGMENT = "_4bV20APMEdyuUt-4qHuVvQ";

	private UMLMarte() {
	}
	
	public static void initMARTE(final ResourceSet resourceSet) {
		final String marteResource = Objects.requireNonNull(UMLMarte.class.getResource("/profiles")).toString();
		final URI marteURI = URI.createURI(marteResource);
		resourceSet.getURIConverter().getURIMap().put(URI.createURI(MARTE_BASE_PATHMAP), marteURI.appendSegment(""));
		final String MARTE_PROFILES_PATHMAP = MARTE_BASE_PATHMAP + MARTE_PROFILE;
		
		// NFP
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(NFPsPackage.eNS_URI, URI.createURI(MARTE_PROFILES_PATHMAP + MARTE_NFP_FRAGMENT));
		resourceSet.getPackageRegistry().put(NFPsPackage.eNS_URI, NFPsPackage.eINSTANCE);
		
		// TIME
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(TimePackage.eNS_URI, URI.createURI(MARTE_PROFILES_PATHMAP + MARTE_TIME_FRAGMENT));
		resourceSet.getPackageRegistry().put(TimePackage.eNS_URI, TimePackage.eINSTANCE);
		
		// GRM
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(GRMPackage.eNS_URI, URI.createURI(MARTE_PROFILES_PATHMAP + MARTE_GRM_FRAGMENT));
		resourceSet.getPackageRegistry().put(GRMPackage.eNS_URI, GRMPackage.eINSTANCE);
		
		// Alloc
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(AllocPackage.eNS_URI, URI.createURI(MARTE_PROFILES_PATHMAP + MARTE_ALLOC_FRAGMENT));
		resourceSet.getPackageRegistry().put(AllocPackage.eNS_URI, AllocPackage.eINSTANCE);
		
		// Core_Elements
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(CoreElementsPackage.eNS_URI, URI.createURI(MARTE_PROFILES_PATHMAP + MARTE_CORE_ELEMENTS_FRAGMENT));
		resourceSet.getPackageRegistry().put(CoreElementsPackage.eNS_URI, CoreElementsPackage.eINSTANCE);
		
		// GQAM
		UMLPlugin.getEPackageNsURIToProfileLocationMap().put(GQAMPackage.eNS_URI, getGqamUri());
		resourceSet.getPackageRegistry().put(GQAMPackage.eNS_URI, GQAMPackage.eINSTANCE);
	}

	public static URI getGqamUri() {
		return URI.createURI("pathmap://Papyrus_PROFILES/MARTE.profile.uml#" + MARTE_GQAM_FRAGMENT);
	}

}
