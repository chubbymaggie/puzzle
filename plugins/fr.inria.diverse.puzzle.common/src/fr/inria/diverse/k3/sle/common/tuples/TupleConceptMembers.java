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
		String members = "";
		for (String member : this.members) {
			members += member + ", ";
		}
		return concept.getName() + " - " + members;
	}
}