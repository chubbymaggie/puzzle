package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;

import fr.inria.diverse.graph.Vertex;

public class EcoreVertex {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private String vertexId;
	private EClassifier classifier;
	private int tarjansIndex;
	private int tarjansLowlink;
	private boolean onTarjansStack;
	private List<EcoreArc> incomingArcs;
	private List<EcoreArc> outgoingArcs;
	private boolean visited;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public EcoreVertex(String vertexId, EClassifier classifier) {
		this.vertexId = vertexId;
		this.classifier = classifier;
		this.tarjansIndex = -1;
		this.tarjansLowlink = -1;
		this.onTarjansStack = false;
		this.incomingArcs = new ArrayList<EcoreArc>();
		this.outgoingArcs = new ArrayList<EcoreArc>();
	}

	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	/**
	 * Indicates if there is a path from the origin to the destination.
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsPath(EcoreVertex destination){
		this.visited = true;
		
		// Base case: there is a direct arc from the origin to the destination. 
		for (EcoreArc ecoreArc : this.getOutgoingArcs()) {
			if(ecoreArc.getTo().getVertexId().equals(destination.getVertexId()))
				return true;
		}
		// Recursive case: visiting recursively the outgoing vertex.
		for (EcoreArc ecoreArc : this.getOutgoingArcs()) {
			if(!ecoreArc.getTo().isVisited()){
				boolean thereIsPath = ecoreArc.getTo().thereIsPath(destination);
				if(thereIsPath)
					return true;
			}
		}
		return false;
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public EClassifier getClassifier() {
		return classifier;
	}

	public String getVertexId() {
		return vertexId;
	}

	public void setVertexId(String vertexId) {
		this.vertexId = vertexId;
	}

	public void setClassifier(EClassifier classifier) {
		this.classifier = classifier;
	}

	public int getTarjansIndex() {
		return tarjansIndex;
	}

	public void setTarjansIndex(int tarjansIndex) {
		this.tarjansIndex = tarjansIndex;
	}

	public int getTarjansLowlink() {
		return tarjansLowlink;
	}

	public void setTarjansLowlink(int tarjansLowlink) {
		this.tarjansLowlink = tarjansLowlink;
	}

	public boolean isOnTarjansStack() {
		return onTarjansStack;
	}

	public void setOnTarjansStack(boolean onTarjansStack) {
		this.onTarjansStack = onTarjansStack;
	}

	public List<EcoreArc> getIncomingArcs() {
		return incomingArcs;
	}

	public List<EcoreArc> getOutgoingArcs() {
		return outgoingArcs;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	
	// -----------------------------------------------
	// Object Methods
	// -----------------------------------------------

	public String toString(){
		return this.vertexId;
	}
	
	public boolean equals(Object o){
		EcoreVertex ecoreVertex = (EcoreVertex) o;
		return ecoreVertex.vertexId.equals(this.vertexId);
	}

	public EcoreVertex cloneVertex() {
		EcoreVertex clone = new EcoreVertex(this.vertexId, this.getClassifier());
		clone.visited = this.visited;
		clone.tarjansIndex = this.tarjansIndex;
		clone.tarjansLowlink = this.tarjansLowlink;
		clone.onTarjansStack = this.onTarjansStack;
		return clone;
	}
}