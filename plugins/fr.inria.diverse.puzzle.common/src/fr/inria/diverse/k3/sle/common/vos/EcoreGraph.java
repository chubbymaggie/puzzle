package fr.inria.diverse.k3.sle.common.vos;

import java.util.ArrayList;

public class EcoreGraph {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private ArrayList<EcoreNode> nodes;
	private ArrayList<EcoreArc> arcs;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public EcoreGraph(){
		this.nodes = new ArrayList<EcoreNode>();
		this.arcs = new ArrayList<EcoreArc>();
	}
	
	// -----------------------------------------------
	// Getters
	// -----------------------------------------------

	public ArrayList<EcoreNode> getNodes() {
		return nodes;
	}

	public ArrayList<EcoreArc> getArcs() {
		return arcs;
	}
}