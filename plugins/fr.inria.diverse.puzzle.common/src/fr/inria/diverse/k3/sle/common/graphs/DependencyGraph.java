package fr.inria.diverse.k3.sle.common.graphs;

import java.util.List;

import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;

/**
 * Class that implements the services of a dependencies graph.
 * 
 * @author David Mendez-Acuna
 *
 */
public class DependencyGraph extends Graph<Vertex, Arc> {

	// -----------------------------------------------
	// Constructors
	// -----------------------------------------------
	
	/**
	 * Constructor by default.
	 */
	public DependencyGraph(){
		super();
	}
	
	/**
	 * Builds a dependency graph from a modularization graph.
	 * @param modularizationGraph. Modularization graph that will be used to create the dependencies graph.
	 */
	public DependencyGraph(EcoreGraph modularizationGraph){
		super();
		
		// Create one vertex for each modularization group. 
		for (EcoreGroup group : modularizationGraph.getGroups()) {
			String moduleName = group.getName();
			DependencyVertex dependencyVertex = new DependencyVertex(moduleName);
			dependencyVertex.getInternalVertex().addAll(group.getVertex());
			group.setDependenciesGraphVertex(dependencyVertex);
			this.vertex.add(dependencyVertex);
		}
		
		// Create one arc for each dependency.
		for (int i = 0; i < vertex.size(); i++) {
			DependencyVertex vertexI = (DependencyVertex) vertex.get(i);
			for (int j = 0; j < vertex.size(); j++) {
				if(i!=j){
					DependencyVertex vertexJ = (DependencyVertex) vertex.get(j);
					if(this.dependsOn(vertexI, vertexJ, modularizationGraph)){
						DependencyArc arc = new DependencyArc(vertexI, vertexJ);
						vertexI.getOutgoingArcs().add(arc);
						vertexJ.getIncomingArcs().add(arc);
						this.arcs.add(arc);
					}
				}
			}
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
	private boolean dependsOn(DependencyVertex origin, DependencyVertex destination, EcoreGraph modularizationGraph){
		for (EcoreArc ecoreArc : modularizationGraph.getArcs()) {
			boolean fromInTheOrigin = exists(origin.getInternalVertex(), ecoreArc.getFrom());
			boolean toInTheDestination = exists(destination.getInternalVertex(), ecoreArc.getTo());
			
			if(fromInTheOrigin && toInTheDestination)
				return true;
		}
		return false;
	}
	
	
	/**
	 * Returns true of the ecore vertex is contained in the ecore vertex list. 
	 * @param internalVertex
	 * @param modularizationGraph
	 * @return
	 */
	private boolean exists(List<EcoreVertex> internalVertex, EcoreVertex vertex) {
		for (EcoreVertex ecoreVertex : internalVertex) {
			if(ecoreVertex.getVertexId().equals(vertex.getVertexId()))
				return true;
		}
		return false;
	}
}