package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

public class ConceptMembersGroupVO {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private EClassifier concept;
	private ArrayList<String> memberGroup;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public ConceptMembersGroupVO(EClassifier concept){
		this.concept = concept;
		memberGroup = new ArrayList<String>();
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

	public ArrayList<String> getMemberGroup() {
		return memberGroup;
	}
	
	public String toString(){
		String members = "";
		for (String member : memberGroup) {
			members += member + ", ";
		}
		return concept.getName() + " - " + members;
	}
}
