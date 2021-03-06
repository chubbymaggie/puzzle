package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

public class TupleConceptMembers {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private EClassifier concept;
	private ArrayList<String> members;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public TupleConceptMembers(EClassifier concept){
		this.concept = concept;
		members = new ArrayList<String>();
	}

	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public EClassifier getConcept() {
		return concept;
	}

	public void setConcept(EClassifier concept) {
		this.concept = concept;
	}

	public ArrayList<String> getMembers() {
		return members;
	}
	
	public String toString(){
		return concept.getName() + ": " + this.getMembersString();
	}
	
	private String getMembersString(){
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
}