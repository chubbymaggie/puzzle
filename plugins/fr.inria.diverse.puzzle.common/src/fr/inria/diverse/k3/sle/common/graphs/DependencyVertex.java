package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;
import java.util.List;

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
	private List<EcoreVertex> internalVertex;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public DependencyVertex(String identifier){
		this.identifier = identifier;
		this.internalVertex = new ArrayList<EcoreVertex>();
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

	public List<EcoreVertex> getInternalVertex() {
		return this.internalVertex;
	}
}