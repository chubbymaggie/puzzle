package fr.inria.diverse.puzzle.variabilityinferer.auxiliar;

import java.util.Hashtable;
import java.util.Iterator;

import vm.BinaryExpression;
import vm.BinaryOperator;
import vm.LanguageConstraint;
import vm.LanguageFeature;
import vm.LanguageFeatureGroup;
import vm.LanguageFeatureModel;
import vm.LanguageFeatureRef;
import vm.UnaryExpression;
import vm.UninaryOperator;
import vm.VmFactory;
import es.us.isa.FAMA.models.FAMAfeatureModel.Dependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.ExcludesDependency;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.RequiresDependency;

/**
 * Offers the services for translating feature models from diverse formats to the VM metamodel.
 * @author David Mendez-Acuna
 *
 */
public class FromFAMAToLanguageFeatureModel {

	// -----------------------------------------------------------
	// Attributes
	// -----------------------------------------------------------
	
	private static FromFAMAToLanguageFeatureModel instance;
	
	// -----------------------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------------------
	
	private FromFAMAToLanguageFeatureModel(){
		
	}
	
	public static FromFAMAToLanguageFeatureModel getInstance(){
		if(instance == null)
			instance = new FromFAMAToLanguageFeatureModel();
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
	public LanguageFeatureModel fromFAMAFeatureModelToFeatureModel(
			FAMAFeatureModel famafm) {
		Hashtable<String, String> famasIndexVsLanguageModules = PCMsGenerator.getInstance().getFamasIndexVsModulesName();
		LanguageFeatureModel fm = VmFactory.eINSTANCE.createLanguageFeatureModel();
		LanguageFeature root = this.fromFAMAFeatureToLanguageFeature(famafm.getRoot());
		fm.setRootFeature(root);
		root.setMandatory(true);
		
		Iterator<Dependency> dependenciesIterator = famafm.getDependencies();
		while (dependenciesIterator.hasNext()) {
			Dependency dependency = (Dependency) dependenciesIterator.next();
			System.out.println("dependency: " + dependency);
			
			// A requires B is the same than A implies B
			if(dependency instanceof RequiresDependency){
				LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
				BinaryExpression expression = VmFactory.eINSTANCE.createBinaryExpression();
				LanguageFeatureRef originRef = VmFactory.eINSTANCE.createLanguageFeatureRef();
				
				String originFeatureName = famasIndexVsLanguageModules.get(dependency.getOrigin().getName());
				if(originFeatureName == null)
					originFeatureName = dependency.getOrigin().getName();
				
				originRef.setRef(this.getLanguageFeatureByName(originFeatureName, root));
				LanguageFeatureRef destinationRef = VmFactory.eINSTANCE.createLanguageFeatureRef();
				
				String destinationFeatureName = famasIndexVsLanguageModules.get(dependency.getDestination().getName());
				if(destinationFeatureName == null)
					destinationFeatureName = dependency.getDestination().getName();
				
				destinationRef.setRef(this.getLanguageFeatureByName(destinationFeatureName, root));
				expression.setLeft(originRef);
				expression.setRight(destinationRef);
				expression.setOperator(BinaryOperator.IMPLIES);
				constraint.setExpression(expression);
				fm.getConstraints().add(constraint);
			}
			// A excludes B is the same than A implies not B
			else if(dependency instanceof ExcludesDependency){
				LanguageConstraint constraint = VmFactory.eINSTANCE.createLanguageConstraint();
				BinaryExpression expression = VmFactory.eINSTANCE.createBinaryExpression();
				LanguageFeatureRef originRef = VmFactory.eINSTANCE.createLanguageFeatureRef();
				
				String originFeatureName = famasIndexVsLanguageModules.get(dependency.getOrigin().getName());
				if(originFeatureName == null)
					originFeatureName = dependency.getOrigin().getName();
				
				originRef.setRef(this.getLanguageFeatureByName(originFeatureName, root));
				
				LanguageFeatureRef destinationRef = VmFactory.eINSTANCE.createLanguageFeatureRef();
				
				String destinationFeatureName = famasIndexVsLanguageModules.get(dependency.getDestination().getName());
				if(destinationFeatureName == null)
					destinationFeatureName = dependency.getDestination().getName();
				
				destinationRef.setRef(this.getLanguageFeatureByName(destinationFeatureName, root));
				UnaryExpression notDestination = VmFactory.eINSTANCE.createUnaryExpression();
				notDestination.setExpr(destinationRef);
				notDestination.setOperator(UninaryOperator.NOT);
				
				expression.setLeft(originRef);
				expression.setRight(notDestination);
				expression.setOperator(BinaryOperator.IMPLIES);
				constraint.setExpression(expression);
				fm.getConstraints().add(constraint);
			}
		}
		return fm;
	}
	
	/**
	 * In-deep translating of the FAMA feature in the parameter to a new puzzle feature.
	 * @param famaFeature. The FAMA feature that should be translated. 
	 * @return
	 */
	private LanguageFeature fromFAMAFeatureToLanguageFeature(Feature famaFeature){
		Hashtable<String, String> famasIndexVsLanguageModules = PCMsGenerator.getInstance().getFamasIndexVsModulesName();
		LanguageFeature pf = VmFactory.eINSTANCE.createLanguageFeature();

		String featureName = famasIndexVsLanguageModules.get(famaFeature.getName());
		if(featureName == null)
			featureName = famaFeature.getName();
		pf.setName(featureName);
		
		Iterator<Relation> relations = famaFeature.getRelations();
		while (relations.hasNext()) {
			Relation currentRelation = (Relation) relations.next();
			LanguageFeatureGroup featureGroup = VmFactory.eINSTANCE.createLanguageFeatureGroup();
			
//			if(currentRelation.isAlternative())
//				featureGroup.setKind(PGroupKind.ALTERNATIVE);
//			else if(currentRelation.isOptional())
//				featureGroup.setKind(PGroupKind.OPTIONAL);
			
			Iterator<Feature> featuresIterator = currentRelation.getDestination();
			while (featuresIterator.hasNext()) {
				Feature childFAMAFeature = (Feature) featuresIterator.next();
				LanguageFeature childPFeatture = this.fromFAMAFeatureToLanguageFeature(childFAMAFeature);
				childPFeatture.setMandatory(currentRelation.isMandatory());
				pf.getChildren().add(childPFeatture);
				featureGroup.getFeatures().add(childPFeatture);
			}
			pf.getGroups().add(featureGroup);
		}
		return pf;
	}
	
	/**
	 * Finds a LanguageFeature by the name in the features model in the parameter.
	 * @param featureName. Name of the feature.
	 * @param featuresModelRoot. Root of the features model where the feature should be searched. 
	 * @return
	 */
	private LanguageFeature getLanguageFeatureByName(String featureName, LanguageFeature featureModelRoot) {
		if(featureModelRoot.getName().equals(featureName)){
			return featureModelRoot;
		}
		for (LanguageFeature feature : featureModelRoot.getChildren()) {
			LanguageFeature found = this.getLanguageFeatureByName(featureName, feature);
			if(found != null)
				return found;
		}
		return null;
	}
}