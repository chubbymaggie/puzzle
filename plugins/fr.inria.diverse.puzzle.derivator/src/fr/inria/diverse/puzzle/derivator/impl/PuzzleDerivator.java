package fr.inria.diverse.puzzle.derivator.impl;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import vm.LanguageFeature;
import vm.LanguageFeatureModel;
import PuzzleADL.InterfaceBinding;
import PuzzleADL.LanguageArchitecture;
import PuzzleADL.LanguageModule;
import PuzzleADL.ProvidedInterface;
import PuzzleADL.RequiredInterface;

/**
 * Implementation of the command DerivateLangaugeFromConfiguration
 * 
 * @author David Mendez-Acuna
 *
 */

public class PuzzleDerivator implements IDerivator{

	// --------------------------------------------------
	// Attributes
	// --------------------------------------------------
	
	private static PuzzleDerivator instance;
	
	// --------------------------------------------------
	// Constructor and singleton
	// --------------------------------------------------
	
	private PuzzleDerivator() {}
	
	public static PuzzleDerivator getInstance(){
		if(instance == null)
			instance = new PuzzleDerivator();
		return instance;
	}
	
	// --------------------------------------------------
	// Methods
	// --------------------------------------------------
	
	@Override
	public void derivateLangaugeFromConfiguration(IProject derivationProject,
			LanguageArchitecture languageArchitectureModel,
			LanguageFeatureModel configuredFeatureModel) {
		
		String melangeFileContents = "package family\n\n";
		
		ArrayList<LanguageFeature> selectedFeatures = new ArrayList<LanguageFeature>();
		this.collectSelectedFeatures(configuredFeatureModel.getRootFeature(), selectedFeatures);
		
		ArrayList<LanguageModule> selectedLanguageModules = this.collectSelectedLangaugeModules(selectedFeatures);
		for (LanguageModule languageModule : selectedLanguageModules) {
			melangeFileContents += this.createLanguageModuleDeclaration(languageModule) + "\n";
			if(languageModule.getRequiredInterface() != null)
				melangeFileContents += this.createModelTypeForRequiredInterface(languageModule.getRequiredInterface()) + "\n";
			if(languageModule.getProvidedInterface() != null)
				melangeFileContents += this.createModelTypeForProvidedInterface(languageModule.getProvidedInterface()) + "\n";
		}
		
		System.out.println("*** THE RESULT FOR MELANGE: ");
		System.out.println(melangeFileContents);
		
		String bindings = this.getBindingsDeclaration(languageArchitectureModel, configuredFeatureModel.getName());
		System.out.println("*** THE RESULT FOR BINDING: ");
		System.out.println(bindings);
	}

	/**
	 * Collects the selected features in the configured feature model, and stores the result in the collection given in the parameter.
	 * @param rootFeature. Root feature of the configured feature model. 
	 * @param selectedFeatures. Collection where the result must be stored. 
	 */
	private void collectSelectedFeatures(
			LanguageFeature rootFeature,
			ArrayList<LanguageFeature> selectedFeatures) {
		// Base case: the root feature is selected, in such a case it is added to the collection
		if(rootFeature.isSelected() && rootFeature.getImplementationModule() != null)
			selectedFeatures.add(rootFeature);
		// Recursive case: scan the child features.
		for (LanguageFeature languageFeature : rootFeature.getChildren()) {
			this.collectSelectedFeatures(languageFeature, selectedFeatures);
		}
	}

	/**
	 * Collects the set of language modules implementing the features included in the collection of language features in the parameter. 
	 * @param selectedFeatures. Collection of language features under study. 
	 * @return
	 */
	private ArrayList<LanguageModule> collectSelectedLangaugeModules(
			ArrayList<LanguageFeature> selectedFeatures) {
		ArrayList<LanguageModule> answer = new ArrayList<LanguageModule>();
		for (LanguageFeature languageFeature : selectedFeatures) {
			answer.add(languageFeature.getImplementationModule());
		}
		return answer;
	}
	
	/**
	 * Creates the definition in melange for a language module given in the parameter.
	 * @param module Language module under study. 
	 * @return
	 */
	private String createLanguageModuleDeclaration(LanguageModule module){
		String answer = "language " + module.getName();
		
		if(module.getProvidedInterface() != null)
			answer += " implements " + module.getProvidedInterface().getName();
		
		if(module.getRequiredInterface() != null)
			answer += " requires " + module.getRequiredInterface().getName();
		
		answer += "{\n";
		answer += "     syntax platform:/resource" + module.getAbstractSyntax().getEcoreRelativePath() + "\n";
		
		if(module.getSemanticsImplementation() != null){
			answer += "\n";
			for(String aspect : module.getSemanticsImplementation().getAspectsIdentifiers()){
				answer += "     with " + aspect + "\n";
			}
		}
		answer += "\n     exacttype " + module.getName() + "MT\n";
		answer += "}\n";
		
		return answer;
	}
	
	/**
	 * Creates a model type's definition from a required interface. 
	 * @param requiredInterface. Required interface under study. 
	 * @return
	 */
	private String createModelTypeForRequiredInterface(
			RequiredInterface requiredInterface) {
		String answer = "modeltype " + requiredInterface.getName() + "{\n";
		answer += "     syntax platform:/resource" + requiredInterface.getEcoreRelativePath() + "\n}\n";
		return answer;
	}
	
	/**
	 * Creates a model type's definition from a provided interface. 
	 * @param providedInterface. Required interface under study. 
	 * @return
	 */
	private String createModelTypeForProvidedInterface(
			ProvidedInterface providedInterface) {
		String answer = "modeltype " + providedInterface.getName() + "{\n";
		answer += "     syntax platform:/resource" + providedInterface.getEcoreRelativePath() + "\n}\n";
		return answer;
	}
	
	/**
	 * Returns a string with the declaration of the bindings from the given language architecture model. 
	 * @param languageArchitectureModel
	 * @return
	 */
	private String getBindingsDeclaration(
			LanguageArchitecture languageArchitectureModel, String familyName) {
		String answer = "package family\n\n";
		answer += "import " + familyName + ".melange\n\n";
		answer += "languageBinding {\n";
		for (InterfaceBinding binding : languageArchitectureModel.getInterfaceBindings()) {
			answer += "     bind( " + binding.getFrom().getName() + ", " + binding.getTo().getName() + ")\n";
		}
		answer += "}\n";
		return answer;
	}
}
