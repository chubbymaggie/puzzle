package fr.inria.diverse.puzzle.variabilityinferer.auxiliar;

import vm.FeatureModel;
import vm.VmFactory;
import vm.VmPackage;
import fr.familiar.variable.FeatureModelVariable;

/**
 * Offers the services for translating feature models from diverse formats to the VM metamodel.
 * @author David Mendez-Acuna
 *
 */
public class FeatureModelsTranslator {

	// -----------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------
	
	private static FeatureModelsTranslator instance;
	
	// -----------------------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------------------
	
	private FeatureModelsTranslator(){
		
	}
	
	public static FeatureModelsTranslator getInstance(){
		if(instance == null)
			instance = new FeatureModelsTranslator();
		return instance;
	}

	// -----------------------------------------------------------
	// Methods
	// -----------------------------------------------------------

	/**
	 * Translates from a features model from FeatureModelVariable (Familiar) to FeatureModel (Puzzle).
	 * @param fmv The feature model as an FeatureModelVariable object.
	 * @return
	 */
	public FeatureModel fromFeatureModelVariableToFeatureModel(
			FeatureModelVariable fmv) {
		FeatureModel fm = VmFactory.eINSTANCE.createFeatureModel();
		
		
		
		return fm;
	}
}