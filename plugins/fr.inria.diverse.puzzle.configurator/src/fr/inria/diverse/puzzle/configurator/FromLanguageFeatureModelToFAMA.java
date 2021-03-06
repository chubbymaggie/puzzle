package fr.inria.diverse.puzzle.configurator;

import vm.LanguageFeature;
import vm.LanguageFeatureGroup;
import vm.LanguageFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.featureModel.Cardinality;

/**
 * Offers the services for translating feature models from diverse formats to the VM metamodel.
 * @author David Mendez-Acuna
 *
 */
public class FromLanguageFeatureModelToFAMA {

	// -----------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------
	
	private static FromLanguageFeatureModelToFAMA instance;
	
	// -----------------------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------------------
	
	private FromLanguageFeatureModelToFAMA(){
		
	}
	
	public static FromLanguageFeatureModelToFAMA getInstance(){
		if(instance == null)
			instance = new FromLanguageFeatureModelToFAMA();
		return instance;
	}

	// -----------------------------------------------------------
	// Methods
	// -----------------------------------------------------------

	/**
	 * Translates from a features model from FAMAFeatureModel (FAMA) to FeatureModel (Puzzle).
	 * @param famafm The feature model as an FAMAFeatureModel object.
	 * @return
	 */
	public FAMAFeatureModel fromLanguageFeatureModelToFAMA(
			LanguageFeatureModel pFeatureModel) {
		
		FAMAFeatureModel famaFeatureModel = new FAMAFeatureModel();
		famaFeatureModel.setRoot(fromFAMAFeatureToLanguageFeature(pFeatureModel.getRootFeature()));
		return famaFeatureModel;
	}
	
	/**
	 * In-deep translating of the FAMA feature in the parameter to a new puzzle feature.
	 * @param famaFeature. The FAMA feature that should be translated. 
	 * @return
	 */
	private Feature fromFAMAFeatureToLanguageFeature(LanguageFeature pFeature){
		Feature feature = new Feature(pFeature.getName());
		
		for (LanguageFeatureGroup group : pFeature.getGroups()) {
			Relation relation = new Relation();
			relation.setParent(feature);
			
			Cardinality cardinality = new Cardinality(group.getCardinality().getLowerBound(), group.getCardinality().getUpperBound());
			relation.addCardinality(cardinality);
			
			for (LanguageFeature groupFeature : group.getFeatures()) {
				Feature relationFeature = this.fromFAMAFeatureToLanguageFeature(groupFeature);
				relation.addDestination(relationFeature);
			}
		}
		
		return feature;
	}
	

}