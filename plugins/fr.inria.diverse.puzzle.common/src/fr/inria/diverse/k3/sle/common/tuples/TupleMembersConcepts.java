package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

public class TupleMembersConcepts {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private ArrayList<String> members;
	private ArrayList<EClassifier> concepts;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------

	public TupleMembersConcepts(ArrayList<String> members) {
		this.members = members;
		this.concepts = new ArrayList<EClassifier>();
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public ArrayList<String> getMembers() {
		return members;
	}

	public ArrayList<EClassifier> getConcepts() {
		return concepts;
	}

	// -----------------------------------------------
	// Methods
	// -----------------------------------------------

	public boolean equals(Object toCompare){
		TupleMembersConcepts toCompareObject = (TupleMembersConcepts) toCompare;
		return toCompareObject.getMembersString().equals(this.getMembersString());
	}
	
	public String getMembersString(){
		String members = "[";
		boolean first = true;
		for (String member : this.members) {
			if(!first) members += ", ";
			members += member;
			first = false;
		}
		members += "]";
		return members;
	}
	
	public String getConceptsString(){
		String concepts = "[";
		boolean first = true;
		for (EClassifier concept : this.concepts) {
			if(!first) concepts += ", ";
			concepts += concept.getName();
			first = false;
		}
		concepts += "]";
		return concepts;
	}
	
	public String toString(){
		return this.getMembersString() + " - " + this.getConceptsString();
	}
}