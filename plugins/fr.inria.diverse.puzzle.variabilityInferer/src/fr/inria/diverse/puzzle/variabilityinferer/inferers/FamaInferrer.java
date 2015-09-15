package fr.inria.diverse.puzzle.variabilityinferer.inferers;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import vm.PBinaryExpression;
import vm.PBinaryOperator;
import vm.PConstraint;
import vm.PFeature;
import vm.PFeatureModel;
import vm.PFeatureRef;
import vm.VmFactory;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import fama.synthesizer.facade.FamaSynthesizer;
import fr.inria.diverse.k3.sle.common.commands.FeaturesModelInference;
import fr.inria.diverse.k3.sle.common.graphs.DependencyArc;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.variabilityinferer.auxiliar.FromFAMAToPFeatureModel;
import fr.inria.diverse.puzzle.variabilityinferer.auxiliar.PCMsGenerator;

/**
 * Implementation of a feature model inferrer that uses the synthesis provided by FAMA.
 * @author David Mendez-Acuna
 *
 */
public class FamaInferrer implements FeaturesModelInference{

	// --------------------------------------------------------
	// Interface methods
	// --------------------------------------------------------
	
	@Override
	public PFeatureModel inferOpenFeaturesModel(IProject targetProject, SynthesisProperties properties,
			ArrayList<Language> languages, EcoreGraph modularizationGraph, DependencyGraph dependenciesGraph) throws Exception {
		String PCM = PCMsGenerator.getInstance().generatePCM(properties, languages, modularizationGraph, PCMsGenerator.FAMA_FORMAT);
		
		File fileReport = new File(targetProject.getLocation().toString()
				+ "/pcm.csv");
		if (!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter(fileReport);
		outRileReport.print(PCM);
		outRileReport.close();
		
		String inputFile = targetProject.getLocation().toString()
				+ "/pcm.csv";
		String outputFile = targetProject.getLocation().toString()
				+ "/variabilityModel.xml";
		
		FAMAFeatureModel famafm = FamaSynthesizer.getInstance().synthesizeFeatureModelFromPCM(inputFile, outputFile);
		PFeatureModel fm = FromFAMAToPFeatureModel.getInstance().fromFAMAFeatureModelToFeatureModel(famafm);
		this.createTechnologicalConstraints(fm, dependenciesGraph);
		return fm;
	}

	@Override
	public PFeatureModel inferClosedFeaturesModel(IProject targetProject,
			SynthesisProperties properties, ArrayList<Language> languages,
			EcoreGraph modularizationGraph, PFeatureModel openFeaturesModel) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	// --------------------------------------------------------
	// Auxiliary methods
	// --------------------------------------------------------
	
	/**
	 * Creates the constraints corresponding to the dependencies between language modules.
	 * @param fm. The feature model that will be enhanced with the technological constraints.
	 * @param modularizationGraph. The modularization graph that is used to obtain the technological constraints.
	 */
	private void createTechnologicalConstraints(PFeatureModel fm,
			DependencyGraph dependenciesGraph) {
		
		for (DependencyArc arc : dependenciesGraph.getArcs()) {
			PConstraint constraint = VmFactory.eINSTANCE.createPConstraint();
			PBinaryExpression expression = VmFactory.eINSTANCE.createPBinaryExpression();
			
			PFeatureRef originRef = VmFactory.eINSTANCE.createPFeatureRef();
			originRef.setRef(this.getPFeatureByName(arc.getFrom().getIdentifier(), fm.getRootFeature()));

			PFeatureRef destinationRef = VmFactory.eINSTANCE.createPFeatureRef();
			destinationRef.setRef(this.getPFeatureByName(arc.getTo().getIdentifier(), fm.getRootFeature()));

			expression.setLeft(originRef);
			expression.setRight(destinationRef);
			expression.setOperator(PBinaryOperator.IMPLIES);
			constraint.setExpression(expression);
			constraint.setName(originRef.getRef().getName() + " => " + destinationRef.getRef().getName());
			fm.getConstraints().add(constraint);
		}
	}
	
	/**
	 * Finds a PFeature by the name in the features model in the parameter.
	 * @param featureName. Name of the feature.
	 * @param featuresModelRoot. Root of the features model where the feature should be searched. 
	 * @return
	 */
	private PFeature getPFeatureByName(String featureName, PFeature featureModelRoot) {
		if(featureModelRoot.getName().equals(featureName)){
			return featureModelRoot;
		}
		for (PFeature feature : featureModelRoot.getChildren()) {
			PFeature found = this.getPFeatureByName(featureName, feature);
			if(found != null)
				return found;
		}
		return null;
	}
}
