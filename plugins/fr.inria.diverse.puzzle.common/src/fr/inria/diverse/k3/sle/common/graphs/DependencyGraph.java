package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object representing a dependency graph.
 * @author David Mendez-Acuna
 *
 */
public class DependencyGraph {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private List<DependencyVertex> vertex;
	private List<DependencyArc> arcs;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public DependencyGraph(){
		vertex = new ArrayList<DependencyVertex>();
		arcs = new ArrayList<DependencyArc>();
	}
	
	// -----------------------------------------------
	// Getters
	// -----------------------------------------------
	
	public List<DependencyVertex> getVertex() {
		return vertex;
	}

	public List<DependencyArc> getArcs() {
		return arcs;
	}
}