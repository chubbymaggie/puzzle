package fr.inria.diverse.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the services of a graph.
 * 
 * @author David Mendez-Acuna
 *
 */
public class Graph<V extends Vertex, A extends Arc> {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	protected List<V> vertex;
	protected List<A> arcs;

	// -----------------------------------------------
	// Constructors
	// -----------------------------------------------
	
	/**
	 * Constructor by default.
	 */
	public Graph(){
		vertex = new ArrayList<V>();
		arcs = new ArrayList<A>();
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	/**
	 * Indicates if there is an arc between the origin and the destination given in the parameters. 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsArc(V origin, V destination){
		for (Arc dependencyArc : arcs) {
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
	public boolean thereIsPath(V origin, V destination){
		this.resetVisited();
		return origin.thereIsPath(destination);
	}
	
	/**
	 * Puts all the visited flag in false for all the vertex in the graph.
	 */
	public void resetVisited(){
		for (V vertex : this.vertex) {
			vertex.setVisited(false);
		}
	}
	
	/**
	 * Indicates if there is any loop in the graph. 
	 * @return
	 */
	public boolean thereIsLoop(){
		for (V vertex : this.vertex) {
			boolean loop = this.thereIsPath(vertex, vertex);
			if(loop)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the list of vertex in the graph such that they have not outgoing arcs.
	 * @return
	 */
	public List<V> getIndendependentVertex(){
		List<V> independentVertex = new ArrayList<V>();
		for (V dependencyVertex : this.vertex) {
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
		for (V currentVertex : this.vertex) {
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
	public List<V> getDirectDependentVertex(
			List<V> vertexList) {
		List<V> directDependentVertex = new ArrayList<V>();
		
		for (int i = 0; i < this.vertex.size(); i++) {
			V originalVertex = this.vertex.get(i);
			for (int j = 0; j < vertexList.size(); j++) {
				V inputVertex = vertexList.get(j);
				if(this.thereIsArc(originalVertex, inputVertex)){
					if(!directDependentVertex.contains(originalVertex)){
						directDependentVertex.add(originalVertex);
					}
				}
			}
		}
		return directDependentVertex;
	}
	
	public String toString(){
		String answer = "Vertex {\n";
		for (V dependencyVertex : vertex) {
			answer += " - " + dependencyVertex.getIdentifier() + "\n";
		}
		answer += "}\n\n";
		answer += "Arcs (" + this.arcs.size() + ") {\n";
		for (Arc dependencyArc : arcs) {
			answer += "  + " + dependencyArc.toString() + "\n";
		}
		answer += "}\n\n";
		return answer;
	}
	
	/**
	 * Returns the arc between the origin and the destination.
	 * Returns null if there is not an arc between the given nodes. 
	 * @param origin
	 * @param destination
	 * @return
	 */
	public Arc getArc(V origin,
			V destination) {
		for (Arc dependencyArc : arcs) {
			if(dependencyArc.getFrom().getIdentifier().equals(origin.getIdentifier()) &&
					dependencyArc.getTo().getIdentifier().equals(destination.getIdentifier()))
				return dependencyArc;
		}
		return null;
	}
	
	/**
	 * Builds and returns the adjacency matrix for the graph. 
	 * @return
	 */
	public String[][] adjacencyMatrix(){
		String[][] matrix = new String[vertex.size() + 1][vertex.size() + 1];
		matrix[0][0] = "V/V";
		
		for (int i = 0; i < vertex.size(); i++) {
			matrix[0][i + 1] = vertex.get(i).getIdentifier();
			matrix[i + 1][0] = vertex.get(i).getIdentifier();
		}
		
		for (int i = 0; i < vertex.size(); i++) {
			V vertexI = vertex.get(i);
			for (int j = 0; j < vertex.size(); j++) {
				V vertexJ = vertex.get(j);
				if(i == j)
					matrix[i + 1][j + 1] = "1";
				else if(this.thereIsArc(vertexI, vertexJ))
					matrix[i + 1][j + 1] = "1";
				else{
					matrix[i + 1][j + 1] = "0";
				}
			}
		}
		
		return matrix;
	}
	
	// -----------------------------------------------
	// Getters
	// -----------------------------------------------
	
	public List<V> getVertex() {
		return vertex;
	}

	public List<A> getArcs() {
		return arcs;
	}
}