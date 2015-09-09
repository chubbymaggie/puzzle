package fr.inria.diverse.k3.sle.common.graphs;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;

/**
 * Value object representing a vertex within a dependency graph.
 * @author David Mendez-Acuna
 *
 */
public class DependencyVertex {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private String identifier;
	private List<EClassifier> eClassifiers;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public DependencyVertex(String identifier){
		this.identifier = identifier;
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public List<EClassifier> geteClassifiers() {
		return eClassifiers;
	}
}