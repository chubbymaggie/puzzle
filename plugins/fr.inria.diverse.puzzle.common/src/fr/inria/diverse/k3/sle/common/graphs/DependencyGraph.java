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
			dependencyVertex.getInternalVertex().addAll(group);
			this.vertex.add(dependencyVertex);
		}
		
		// Create one arc for each dependency.
		for (int i = 0; i < vertex.size(); i++) {
			DependencyVertex vertexI = vertex.get(i);
			for (int j = 0; j < vertex.size(); j++) {
				if(i!=j){
					DependencyVertex vertexJ = vertex.get(j);
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

	/**
	 * Indicates if there is an arc between the origin and the destination given in the parameters. 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsArc(DependencyVertex origin, DependencyVertex destination){
		for (DependencyArc dependencyArc : arcs) {
			if(dependencyArc.getFrom().getIdentifier().equals(origin.getIdentifier()) &&
					dependencyArc.getTo().getIdentifier().equals(destination.getIdentifier()))
				return true;
		}
		return false;
	}
	
	/**
	 * Indicates if there is a path from the origin to the destination.
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsPath(DependencyVertex origin, DependencyVertex destination){
		this.resetVisited();
		return origin.thereIsPath(destination);
	}
	
	/**
	 * Puts all the visited flag in false for all the vertex in the graph.
	 */
	public void resetVisited(){
		for (DependencyVertex vertex : this.vertex) {
			vertex.setVisited(false);
		}
	}
	
	/**
	 * Indicates if there is any loop in the graph. 
	 * @return
	 */
	public boolean thereIsLoop(){
		for (DependencyVertex vertex : this.vertex) {
			boolean loop = this.thereIsArc(vertex, vertex);
			if(loop)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the list of vertex in the graph such that they have not outgoing arcs.
	 * @return
	 */
	public List<DependencyVertex> getIndendependentVertex(){
		List<DependencyVertex> independentVertex = new ArrayList<DependencyVertex>();
		for (DependencyVertex dependencyVertex : this.vertex) {
			if(dependencyVertex.getOutgoingArcs().size() == 0)
				independentVertex.add(dependencyVertex);
		}
		return independentVertex;
	}
	
	/**
	 * Indicates if the flag 'included' is true for all the vertex in the graph.
	 * @return
	 */
	public boolean allIncluded() {
		for (DependencyVertex currentVertex : this.vertex) {
			if(!currentVertex.isIncluded())
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the list of vertex that directly depend on at least one vertex in the list in the parameter.
	 * @param currentLevel
	 * @return
	 */
	public List<DependencyVertex> getDirectDependentVertex(
			List<DependencyVertex> vertexList) {
		List<DependencyVertex> directDependentVertex = new ArrayList<DependencyVertex>();
		
		for (int i = 0; i < this.vertex.size(); i++) {
			DependencyVertex originalVertex = this.vertex.get(i);
			for (int j = 0; j < vertexList.size(); j++) {
				DependencyVertex inputVertex = vertexList.get(j);
				if(this.thereIsArc(originalVertex, inputVertex)){
					directDependentVertex.add(originalVertex);
				}
			}
		}
		return directDependentVertex;
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