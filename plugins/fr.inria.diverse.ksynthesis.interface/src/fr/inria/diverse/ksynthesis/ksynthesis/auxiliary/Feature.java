package fr.inria.diverse.ksynthesis.ksynthesis.auxiliary;

/**
 * Class that represents a feature in PCMs for KSynthesis.
 * @author David Mendez-Acuna
 */
public class Feature {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	
	private String identifier;

	// ----------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------
	
	public Feature(String identifier) {
		super();
		this.identifier = identifier;
	}

	// ----------------------------------------------------------
	// Getters and setters
	// ----------------------------------------------------------
	
	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
}