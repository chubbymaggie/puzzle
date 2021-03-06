package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

public class TupleModuleConceptsMembers {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private String moduleName;
	private ArrayList<EClassifier> concepts;
	private String members;

	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public TupleModuleConceptsMembers(String moduleName, String members) {
		this.moduleName = moduleName;
		this.members = members;
		this.concepts = new ArrayList<EClassifier>();
	}

	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public ArrayList<EClassifier> getConcepts() {
		return concepts;
	}
	
	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}

	public String toString(){
		String concepts = "";
		for (EClassifier eClassifier : this.concepts) {
			concepts += eClassifier.getName() + ", ";
		}
		return this.moduleName + " - " + concepts;
	}
}