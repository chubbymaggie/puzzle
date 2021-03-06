package fr.inria.diverse.puzzle.derivator.impl;

import java.io.IOException;
import org.eclipse.core.resources.IProject;
import vm.LanguageFeatureModel;
import PuzzleADL.LanguageArchitecture;

/**
 * Interface to the command DerivateLangaugeFromConfiguration
 * 
 * @author David Mendez-Acuna
 *
 */
public interface IDerivator {

	// --------------------------------------------------
	// Methods
	// --------------------------------------------------
	
	/**
	 * Derives a language from a given configuration.
	 * 
	 * @param derivationProject Project where the language must me derived
	 * @param languageArchitectureModel Language architecture model
	 * @param configuredFeatureModel Configured feature model.
	 * @throws IOException  In the case of any input/output errors during the derivation files process. 
	 */
	public void derivateLangaugeFromConfiguration(IProject derivationProject, 
			LanguageArchitecture languageArchitectureModel, LanguageFeatureModel configuredFeatureModel) throws IOException;
}
