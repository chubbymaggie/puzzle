package fr.inria.diverse.puzzle.variabilityinferer;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;

import fr.inria.diverse.k3.sle.common.commands.VariabilityInferer;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class VariabilityInfererManager {

	// --------------------------------------------------
	// Attributes
	// --------------------------------------------------
	
	private static VariabilityInfererManager instance;
	
	// --------------------------------------------------
	// Constructor and singleton
	// --------------------------------------------------
	
	private VariabilityInfererManager(){
		
	}
	
	public static VariabilityInfererManager getInstance(){
		if(instance == null){
			instance = new VariabilityInfererManager();
		}
		return instance;
	}
	
	// --------------------------------------------------
	// Methods
	// --------------------------------------------------
	
	public void synthesizeVariabilityModel(SynthesisProperties synthesisProperties, ArrayList<Language> languages, EcoreGraph modularizationGraph){
		VariabilityInferer inferrer = synthesisProperties.getVariabilityInferer();
		EObject variabilityModel = inferrer.inferVariabilityModel(synthesisProperties, languages, modularizationGraph);
		System.out.println("variabilityModel: " + variabilityModel);
		//TODO serialize the model
	}
}