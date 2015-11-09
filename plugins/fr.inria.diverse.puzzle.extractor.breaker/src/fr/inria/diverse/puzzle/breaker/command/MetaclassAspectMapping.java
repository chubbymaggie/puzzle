package fr.inria.diverse.puzzle.breaker.command;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClassifier;

import fr.inria.diverse.melange.metamodel.melange.Aspect;

public class MetaclassAspectMapping {

	// ----------------------------------------------
	// Attributes
	// ----------------------------------------------
	
	private EClassifier metaclass;
	private ArrayList<Aspect> aspects;
	
	// ----------------------------------------------
	// Constructor
	// ----------------------------------------------
	
	public MetaclassAspectMapping(EClassifier metaclass){
		this.metaclass = metaclass;
		this.aspects = new ArrayList<Aspect>();
	}
	
	public MetaclassAspectMapping(EClassifier metaclass, Aspect aspect){
		this.metaclass = metaclass;
		this.aspects = new ArrayList<Aspect>();
		this.aspects.add(aspect);
	}
	
	// ----------------------------------------------
	// Getters and setters
	// ----------------------------------------------

	public EClassifier getMetaclass() {
		return metaclass;
	}
	
	public void setMetaclass(EClassifier metaclass) {
		this.metaclass = metaclass;
	}
	
	public ArrayList<Aspect> getAspects() {
		return aspects;
	}
	
	public void setAspects(ArrayList<Aspect> aspects) {
		this.aspects = aspects;
	}
	
	// ----------------------------------------------
	// Methods
	// ----------------------------------------------
	
	public boolean equals(Object o){
		MetaclassAspectMapping mapping = (MetaclassAspectMapping) o;
		return mapping.getMetaclass().getName().equals(metaclass.getName());
	}
}
