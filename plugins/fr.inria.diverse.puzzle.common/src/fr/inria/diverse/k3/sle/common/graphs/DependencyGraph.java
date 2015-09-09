package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the services of a dependencies graph.
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
	// Constructors
	// -----------------------------------------------
	
	/**
	 * Constructor by default.
	 */
	public DependencyGraph(){
		vertex = new ArrayList<DependencyVertex>();
		arcs = new ArrayList<DependencyArc>();
	}
	
	/**
	 * Builds a dependency graph from a modularization graph.
	 * @param modularizationGraph. Modularization graph that will be used to create the dependencies graph.
	 */
	public DependencyGraph(EcoreGraph modularizationGraph){
		vertex = new ArrayList<DependencyVertex>();
		arcs = new ArrayList<DependencyArc>();
		
		// Create one vertex for each modularization group. 
		for (ArrayList<EcoreVertex> group : modularizationGraph.getGroups()) {
			String moduleName = EcoreGraph.getLanguageModuleName(group);
			DependencyVertex dependencyVertex = new DependencyVertex(moduleName);
			dependencyVertex.geteClassifiers().addAll(EcoreGraph.collectEClassifierByGroup(group));
			this.vertex.add(dependencyVertex);
		}
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	/**
	 * Indicates if the origin depends on the destination.
	 * In other words, returns true if there is at least one eClassifier in the origin that contains a reference to
	 * a classifier in the destination.
	 * 
	 * @param origin
	 * @param destination
	 * @return
	 */
	private boolean dependsOn(DependencyVertex origin, DependencyVertex destination){
		
		return false;
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