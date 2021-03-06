package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.xtext.common.types.JvmDeclaredType;

public class TupleConceptMethodsMembers {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private JvmDeclaredType concept;
	private ArrayList<TupleMethodMembers> methodsMembers;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public TupleConceptMethodsMembers(JvmDeclaredType concept){
		this.concept = concept;
		this.methodsMembers = new ArrayList<TupleMethodMembers>();
	}

	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public JvmDeclaredType getConcept() {
		return concept;
	}

	public void setConcept(JvmDeclaredType concept) {
		this.concept = concept;
	}

	public ArrayList<TupleMethodMembers> getMethodsMembers() {
		return methodsMembers;
	}
}