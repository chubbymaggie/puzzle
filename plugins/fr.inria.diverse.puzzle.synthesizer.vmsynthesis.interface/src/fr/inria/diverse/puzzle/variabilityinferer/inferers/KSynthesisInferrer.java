package fr.inria.diverse.puzzle.variabilityinferer.inferers;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;

import vm.LanguageFeatureModel;
import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Graph;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.commands.FeaturesModelInference;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties;
import fr.inria.diverse.ksynthesis.ksynthesis.facade.KSynthesisSynthesizer;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.variabilityinferer.auxiliar.PCMsGenerator;

/**
 * Implementation of a feature model inferrer that uses KSynthesis.
 * @author David Mendez-Acuna
 *
 */
public class KSynthesisInferrer implements FeaturesModelInference {

	@Override
	public LanguageFeatureModel inferOpenFeaturesModel(IProject targetProject, SynthesisProperties properties, 
			ArrayList<Language> languages, EcoreGraph modularizationGraph, Graph<Vertex, Arc> dependenciesGraph) throws Exception {
		String PCM = PCMsGenerator.getInstance().generatePCM(properties, languages, modularizationGraph, PCMsGenerator.OPEN_COMPARE_FORMAT);
		
		File fileReport = new File(targetProject.getLocation().toString()
				+ "/pcm.csv");
		if (!fileReport.exists())
			fileReport.createNewFile();
		PrintWriter outRileReport = new PrintWriter(fileReport);
		outRileReport.print(PCM);
		outRileReport.close();
		
		LanguageFeatureModel fmv = KSynthesisSynthesizer.getInstance().synthesizeFeatureModelFromPCM(targetProject.getLocation().toString()
				+ "/pcm.csv", targetProject.getLocation().toString() + "/vm.xml");
		return fmv;
	}

	@Override
	public LanguageFeatureModel inferClosedFeaturesModel(IProject targetProject,
			SynthesisProperties properties, ArrayList<Language> languages,
			EcoreGraph modularizationGraph, LanguageFeatureModel openFeaturesModel) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
