package fr.inria.diverse.puzzle.variabilityinferer;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import vm.PFeature;
import vm.PFeatureModel;
import vm.PLanguageModule;
import vm.VmFactory;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.commands.FeaturesModelInference;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Delegator for variability models inference.
 * @author David Mendez-Acuna
 */
public class VariabilityInfererManager {

	// --------------------------------------------------
	// Attributes
	// --------------------------------------------------

	private static VariabilityInfererManager instance;

	// --------------------------------------------------
	// Constructor and singleton
	// --------------------------------------------------

	private VariabilityInfererManager() {

	}

	public static VariabilityInfererManager getInstance() {
		if (instance == null) {
			instance = new VariabilityInfererManager();
		}
		return instance;
	}

	// --------------------------------------------------
	// Methods
	// --------------------------------------------------

	/**
	 * Synthesizes and returns the open features model. 
	 * @param synthesisProperties
	 * @param languages
	 * @param modularizationGraph
	 * @param dependenciesGraph
	 * @param project
	 * @throws Exception
	 */
	public PFeatureModel synthesizeOpenFeaturesModel(SynthesisProperties synthesisProperties,
			ArrayList<Language> languages, EcoreGraph modularizationGraph, Graph<Vertex, Arc> dependenciesGraph,
			IProject project) throws Exception {
		
		FeaturesModelInference inferrer = synthesisProperties
				.getVariabilityInferer();
		
		PFeatureModel openFeaturesModel = inferrer.inferOpenFeaturesModel(project, 
				synthesisProperties, languages, modularizationGraph, dependenciesGraph);

		this.addModulesInformation(openFeaturesModel, dependenciesGraph);
		
		return openFeaturesModel;
	}

	/**
	 * Completes the feature model in the parameter with the information of the language modules
	 * 	i.e., the implementation of the language features.
	 * @param featureModel
	 * @param modularizationGraph
	 */
	private void addModulesInformation(PFeatureModel featureModel,
			Graph<Vertex, Arc> dependenciesGraph) {
		this.findLanguageModule(featureModel.getRootFeature(), dependenciesGraph);
	}

	/**
	 * Finds and complete a feature with the information of its implementation i.e., the language module. 
	 * Then, it advances in the features tree by invoking this method recursively. 
	 * @param rootFeature
	 * @param modularizationGraph
	 */
	private void findLanguageModule(PFeature rootFeature,
			Graph<Vertex, Arc> dependenciesGraph) {
		
		Vertex vertex = dependenciesGraph.searchVertexByIdentifier(rootFeature.getName());
		
		if(vertex != null){
			PLanguageModule languageModule = VmFactory.eINSTANCE.createPLanguageModule();
			languageModule.setName(vertex.getIdentifier());
			rootFeature.setImplementationModule(languageModule);
		}
		
		// Recursion
		for(PFeature child : rootFeature.getChildren()){
			this.findLanguageModule(child, dependenciesGraph);
		}
	}

	/**
	 * Synthesizes and returns the closed features model.
	 * @param synthesisProperties
	 * @param languages
	 * @param modularizationGraph
	 * @param dependenciesGraph
	 * @param project
	 * @param openFeaturesModel
	 * @return
	 * @throws Exception
	 */
	public PFeatureModel synthesizeClosedFeaturesModel(
			SynthesisProperties synthesisProperties, ArrayList<Language> languages,
			EcoreGraph modularizationGraph, DependencyGraph dependenciesGraph,
			IProject project, PFeatureModel openFeaturesModel) throws Exception {
		
		FeaturesModelInference inferrer = synthesisProperties
				.getVariabilityInferer();
		
		PFeatureModel closedFeaturesModel = inferrer.inferClosedFeaturesModel(
				project, synthesisProperties, languages, modularizationGraph, openFeaturesModel);

		return closedFeaturesModel;
	}
	
	
}