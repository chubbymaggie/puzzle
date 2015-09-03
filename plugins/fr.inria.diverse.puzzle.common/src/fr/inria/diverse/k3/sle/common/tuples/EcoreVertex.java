package fr.inria.diverse.k3.sle.common.tuples;

import org.eclipse.emf.ecore.EClassifier;

public class EcoreVertex {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private String vertexId;
	private EClassifier classifier;
	private int tarjansIndex;
	private int tarjansLowlink;
	private boolean onTarjansStack;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public EcoreVertex(String vertexId, EClassifier classifier) {
		this.vertexId = vertexId;
		this.classifier = classifier;
		this.tarjansIndex = -1;
		this.tarjansLowlink = -1;
		this.onTarjansStack = false;
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
}