/*
 * generated by Xtext
 */
package fr.inria.diverse.puzzle.language;

/**
 * Initialization support for running Xtext languages 
 * without equinox extension registry
 */
public class BindingStandaloneSetup extends BindingStandaloneSetupGenerated{

	public static void doSetup() {
		new BindingStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
}

