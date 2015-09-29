package fr.inria.diverse.generator.pcm;

import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;

public class InstanceVO {

	// -------------------------------------------------
	// Attributes
	// -------------------------------------------------
	
	private Graph<Vertex, Arc> dependenciesGraph;
	private String openPCM;
	private String closedPCM;
	
	
	// -------------------------------------------------
	// Constructor
	// -------------------------------------------------
	
	public InstanceVO(Graph<Vertex, Arc> dependenciesGraph, String openPCM,
			String closedPCM) {
		super();
		this.dependenciesGraph = dependenciesGraph;
		this.openPCM = openPCM;
		this.closedPCM = closedPCM;
	}
	
	// -------------------------------------------------
	// Getters and setters
	// -------------------------------------------------
	
	public Graph<Vertex, Arc> getDependenciesGraph() {
		return dependenciesGraph;
	}


	public void setDependenciesGraph(Graph<Vertex, Arc> dependenciesGraph) {
		this.dependenciesGraph = dependenciesGraph;
	}


	public String getOpenPCM() {
		return openPCM;
	}


	public void setOpenPCM(String openPCM) {
		this.openPCM = openPCM;
	}


	public String getClosedPCM() {
		return closedPCM;
	}


	public void setClosedPCM(String closedPCM) {
		this.closedPCM = closedPCM;
	}
}