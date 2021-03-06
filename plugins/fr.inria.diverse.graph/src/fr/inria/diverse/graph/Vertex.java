package fr.inria.diverse.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object representing a vertex within a graph.
 * 
 * @author David Mendez-Acuna
 *
 */
public class Vertex {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	protected String identifier;
	protected List<Arc> incomingArcs;
	protected List<Arc> outgoingArcs;
	protected boolean visited;
	protected boolean included;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public Vertex(String identifier){
		this.identifier = identifier;
		this.incomingArcs = new ArrayList<Arc>();
		this.outgoingArcs = new ArrayList<Arc>();
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	/**
	 * Indicates if there is a path from the origin to the destination.
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsPath(Vertex destination){
		this.visited = true;
		
		// Base case: there is a direct arc from the origin to the destination. 
		for (Arc ecoreArc : this.getOutgoingArcs()) {
			if(ecoreArc.getTo().getIdentifier().equals(destination.getIdentifier()))
				return true;
		}
		// Recursive case: visiting recursively the outgoing vertex.
		for (Arc ecoreArc : this.getOutgoingArcs()) {
			if(!ecoreArc.getTo().isVisited()){
				boolean thereIsPath = ecoreArc.getTo().thereIsPath(destination);
				if(thereIsPath)
					return true;
			}
		}
		return false;
	}
	
	public String toString(){
		return this.identifier;
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

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public List<Arc> getIncomingArcs() {
		return incomingArcs;
	}

	public List<Arc> getOutgoingArcs() {
		return outgoingArcs;
	}

	public boolean isIncluded() {
		return included;
	}

	public void setIncluded(boolean included) {
		this.included = included;
	}
}