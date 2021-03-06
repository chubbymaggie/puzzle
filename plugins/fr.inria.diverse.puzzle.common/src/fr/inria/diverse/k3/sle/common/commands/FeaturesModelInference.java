package fr.inria.diverse.k3.sle.common.commands;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import vm.LanguageFeatureModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Contract for the variability inferrer command. 
 * @author David Mendez-Acuna
 */
public interface FeaturesModelInference {

	/**
	 * Returns a feature model that exploits the variability in the family of DSLs under study.
	 * That is, it returns a features model that represents all the variability points that can be extracted 
	 * 		from the modularization graph and permits new configurations. 
	 * 
	 * @param targetProject. Project where the feature model should be created. 
	 * @param properties. Synthesis properties.
	 * @param languages. Family of DSLs under study.
	 * @param modularizationGraph. Modularization graph.
	 * @return
	 * @throws Exception
	 */
	public LanguageFeatureModel inferOpenFeaturesModel(IProject targetProject, 
			SynthesisProperties properties, ArrayList<Language> languages, 
			EcoreGraph modularizationGraph, Graph<Vertex, Arc> dependenciesGraph) throws Exception;
	
	/**
	 * Returns a feature model that strictly represents the family of DSLs under study.
	 * That is, it returns a features model that only the current family members can be configured. 
	 * 
	 * @param targetProject. Project where the feature model should be created. 
	 * @param properties. Synthesis properties.
	 * @param languages. Family of DSLs under study.
	 * @param modularizationGraph. Modularization graph.
	 * @return
	 * @throws Exception
	 */
	public LanguageFeatureModel inferClosedFeaturesModel(IProject targetProject,
			SynthesisProperties properties, ArrayList<Language> languages,
			EcoreGraph modularizationGraph, LanguageFeatureModel openFeaturesModel) throws Exception;
}
