package fr.inria.diverse.ksynthesis.ksynthesis.auxiliary;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents a product in PCMs for KSynthesis.
 * @author David Mendez-Acuna
 */
public class Product {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	
	private String identifier;

	private List<Feature> features;
	
	// ----------------------------------------------------------
	// Constructor
	// ----------------------------------------------------------
	
	public Product(String identifier) {
		super();
		this.identifier = identifier;
		this.features = new ArrayList<Feature>();
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

	public List<Feature> getFeatures() {
		return features;
	}
}