package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;

import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmOperation;

public class TupleConceptMethodMembers {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private JvmDeclaredType concept;
	private JvmOperation method;
	private ArrayList<String> members;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public TupleConceptMethodMembers(JvmDeclaredType concept, JvmOperation method){
		this.concept = concept;
		this.method = method;
		members = new ArrayList<String>();
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

	public JvmOperation getMethod() {
		return method;
	}

	public void setMethod(JvmOperation method) {
		this.method = method;
	}

	public ArrayList<String> getMembers() {
		return members;
	}
	
	public String toString(){
		String members = "";
		for (String member : this.members) {
			members += member + ", ";
		}
		return concept.getSimpleName() + "." + method.getSimpleName() + " - " + members;
	}
}