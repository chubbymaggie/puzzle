package fr.inria.diverse.puzzle.synthesizer.impl;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import PuzzleADL.AbstractSyntaxImplementation;
import PuzzleADL.InterfaceBinding;
import PuzzleADL.LanguageArchitecture;
import PuzzleADL.LanguageModule;
import PuzzleADL.ProvidedInterface;
import PuzzleADL.PuzzleADLFactory;
import PuzzleADL.RequiredInterface;
import vm.LanguageFeatureModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGroup;
import fr.inria.diverse.k3.sle.common.utils.ModelUtils;
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.breaker.command.BreakerImpl;
import fr.inria.diverse.puzzle.metrics.managers.ProductLinesMetricManager;
import fr.inria.diverse.puzzle.variabilityinferer.VariabilityInfererManager;

public class SynthesizerManager {

	// ----------------------------------------------------------
	// Attributes
	// ----------------------------------------------------------
	
	private static SynthesizerManager instance;
	
	// ----------------------------------------------------------
	// Constructor and singleton
	// ----------------------------------------------------------
	
	private SynthesizerManager(){
		
	}
	
	public static SynthesizerManager getInstance(){
		if(instance == null)
			instance = new SynthesizerManager();
		return instance;
	}
	
	// ----------------------------------------------------------
	// Methods
	// ----------------------------------------------------------
	
	/**
	 * Synthesizes a language product line from the commonalities and variability
	 * existing in the set of legacy DSLs given in the parameter and according the reverse-engineering
	 * properties in the properties configuration object.
	 * 
	 * @param properties
	 * @param languages
	 * @param project
	 * @throws Exception
	 */
	public void synthesizeLanguageProductLine(SynthesisProperties properties, ArrayList<Language> languages, IProject project) throws Exception{
		
		// Step 1.1: Break-down the family
		EcoreGraph modularizationGraph = BreakerImpl.getInstance().breakDownFamily(languages, properties, project);
		
		// Step 1.2: Generate reports with modularization metrics and dependencies graph visualizers
		ProductLinesMetricManager metricsManager = new ProductLinesMetricManager(project);
		metricsManager.createProductLineCouplingReport(languages);
		metricsManager.createProductLineCouplingReportData(languages, properties.getConceptComparisonOperator(), 
				properties.getMethodComparisonOperator(), modularizationGraph);
		metricsManager.createProductLineIntraconnectivityReport(languages);
		metricsManager.createProductLineIntraConnectivityReportData(languages, properties.getConceptComparisonOperator(), 
				properties.getMethodComparisonOperator(), modularizationGraph);
		metricsManager.createProductLineInterconnectivityReport(languages);
		metricsManager.createProductLineInterConnectivityReportData(languages, properties.getConceptComparisonOperator(), 
				properties.getMethodComparisonOperator(), modularizationGraph);
		
		// Step 1.3: Compute the dependencies graph.
		DependencyGraph dependenciesGraph = new DependencyGraph(modularizationGraph);
		metricsManager.createDependenciesGraph();
		metricsManager.createDependenciesGraphData(languages, properties.getConceptComparisonOperator(), 
				dependenciesGraph);
		
		// Step 1.4: Validates that the dependencies graph is acyclic.
		if(dependenciesGraph.thereIsLoop())
			throw new Exception("The obtained dependencies graph is not acyclic! Check your graph partitioning algorithm.");
		
		// Step 1.5: Computes the ADL script to explicitly serialize the architecture model
		LanguageArchitecture languageArchitectureModel = this.createLanguageArchitectureModel("Architecture", modularizationGraph, dependenciesGraph);
		String modelFile = project.getLocation().toString() + "/models/1-LanguagesArchitectureModel.puzzleadl";
		ModelUtils.saveXMIFile(languageArchitectureModel, modelFile);
		metricsManager.createModulesReport(languageArchitectureModel);
		
		// Step 2.1: Synthesize the open variability model i.e., the one that only contains
		//			 the technological constraints so it explotes the variability.
		LanguageFeatureModel openFeaturesModel = VariabilityInfererManager.getInstance().synthesizeOpenFeaturesModel(
				properties, languages, modularizationGraph, dependenciesGraph, project, languageArchitectureModel);
		ModelUtils.saveXMIFile(openFeaturesModel, project.getLocation() + "/models/2-LanguagesVariabilityOpenModel.vm");
		
		// Step 2.1: Synthesize the closed variability model i.e., the one that contains
		//			 not only the technological constraints but also considers the PCM.
		LanguageFeatureModel closedFeaturesModel = VariabilityInfererManager.getInstance().synthesizeClosedFeaturesModel(
				properties, languages, modularizationGraph, dependenciesGraph, project, openFeaturesModel);
		ModelUtils.saveXMIFile(closedFeaturesModel, project.getLocation() + "/models/3-LanguagesVariabilityClosedModel.vm");

		// Step 4: Refresh the product line project. 
		ProjectManagementServices.refreshProject(project);
	}
	
	/**
	 * Produces a language architecture model according to the information in the
	 * modularization graph given in the parameter. 
	 * 
	 * @param modularizationGraph
	 * @return
	 */
	private LanguageArchitecture createLanguageArchitectureModel(String languageName,
			EcoreGraph modularizationGraph, DependencyGraph dependenciesGraph) {
		LanguageArchitecture languageArchitectureModel = PuzzleADLFactory.eINSTANCE.createLanguageArchitecture();
		
		// Creating the one language module for each ecore constructs group.
		for (EcoreGroup group : modularizationGraph.getGroups()) {
			LanguageModule languageModule = PuzzleADLFactory.eINSTANCE.createLanguageModule();
			languageModule.setName(group.getName());
			
			AbstractSyntaxImplementation as = PuzzleADLFactory.eINSTANCE.createAbstractSyntaxImplementation();
			as.setEcorePath(group.getMetamodelPath());
			as.setEcoreRelativePath(group.getMetamodelPath().replace(group.getImplementationProjectLocation(), ""));
			languageModule.setAbstractSyntax(as);
			
			if(group.getRequiredInterfacePath() != null){
				RequiredInterface requiredInterface = PuzzleADLFactory.eINSTANCE.createRequiredInterface();
				requiredInterface.setName(group.getName() + "Req");
				requiredInterface.setEcorePath(group.getRequiredInterfacePath());
				requiredInterface.setEcoreRelativePath(group.getMetamodelPath().replace(group.getImplementationProjectLocation(), ""));
				languageModule.setRequiredInterface(requiredInterface);
			}
			
			if(group.getProvidedInterfacePath() != null){
				ProvidedInterface providedInterface = PuzzleADLFactory.eINSTANCE.createProvidedInterface();
				providedInterface.setName(group.getName() + "Prov");
				providedInterface.setEcorePath(group.getProvidedInterfacePath());
				providedInterface.setEcoreRelativePath(group.getMetamodelPath().replace(group.getImplementationProjectLocation(), ""));
				languageModule.setProvidedInterface(providedInterface);
			}
			
			languageArchitectureModel.getLanguageModules().add(languageModule);
		}
		
		// Creating dependencies between modules i.e., binding between interfaces according to the arcs in the 
		// dependencies graph.
		for (Arc arc : dependenciesGraph.getArcs()) {
			EcoreGroup fromGroup = modularizationGraph.getEcoreGroupByDependencyNode(arc.getFrom());
			EcoreGroup toGroup = modularizationGraph.getEcoreGroupByDependencyNode(arc.getTo());
			
			LanguageModule fromModule = this.getLanguageModuleByEcoreGroup(languageArchitectureModel, fromGroup);
			LanguageModule toModule = this.getLanguageModuleByEcoreGroup(languageArchitectureModel, toGroup);
			
			if(fromModule.getRequiredInterface() != null && toModule.getProvidedInterface() != null){
				InterfaceBinding binding = PuzzleADLFactory.eINSTANCE.createInterfaceBinding();
				binding.setFrom(fromModule.getRequiredInterface());
				binding.setTo(toModule.getProvidedInterface());
				languageArchitectureModel.getInterfaceBindings().add(binding);
			}
		}
		return languageArchitectureModel;
	}

	/**
	 * Returns the language module created for the ecore group in the parameter. 
	 * @param languageArchitectureModel 
	 * @param group
	 * @return
	 */
	private LanguageModule getLanguageModuleByEcoreGroup(LanguageArchitecture languageArchitectureModel, EcoreGroup group) {
		for (LanguageModule module : languageArchitectureModel.getLanguageModules()) {
			if(module.getName().equals(group.getName()))
				return module;
		}
		return null;
	}
}