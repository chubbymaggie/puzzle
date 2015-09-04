package fr.inria.diverse.puzzle.metrics.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.comparisonOperators.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.DeepConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.MethodComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.NamingMethodComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.SignatureAndSourceMethodComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.SignatureMethodComparison;
import fr.inria.diverse.k3.sle.common.utils.ModelUtils;
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices;
import fr.inria.diverse.melange.metamodel.melange.Element;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.melange.metamodel.melange.ModelTypingSpace;
import fr.inria.diverse.puzzle.metrics.chartMetrics.ChartMetric;
import fr.inria.diverse.puzzle.metrics.chartMetrics.IndividualizationRatio;
import fr.inria.diverse.puzzle.metrics.chartMetrics.MaintananceCosts;
import fr.inria.diverse.puzzle.metrics.chartMetrics.PairwiseRelationshipRatio;
import fr.inria.diverse.puzzle.metrics.chartMetrics.ProductRelatedReusability;
import fr.inria.diverse.puzzle.metrics.chartMetrics.SizeOfCommonality;
import fr.inria.diverse.puzzle.metrics.managers.FamilysMetricManager;
import fr.inria.diverse.puzzle.metrics.specialCharts.DependenciesGraph;
import fr.inria.diverse.puzzle.metrics.specialCharts.FamilyMembershipGraph;
import fr.inria.diverse.puzzle.metrics.specialCharts.SemanticalVariabilityTree;
import fr.inria.diverse.puzzle.metrics.specialCharts.SemanticalVennDiagram;
import fr.inria.diverse.puzzle.metrics.specialCharts.SyntacticVennDiagram;
import fr.inria.diverse.puzzle.metrics.specialCharts.TarjansAlgorithmGraph;

public class ComputeMetricsActionImpl {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private static ComputeMetricsActionImpl instance;
	
	// -----------------------------------------------
	// Constructor and singleton
	// -----------------------------------------------
	
	private ComputeMetricsActionImpl(){}
	
	public static ComputeMetricsActionImpl getInstance(){
		if(instance == null)
			instance = new ComputeMetricsActionImpl();
		return instance;
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	/**
	 * Computes the metrics defined in Mendez-Acuna et. al and generates the corresponding report in HTML. 
	 * @param selectedResource
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 * @throws URISyntaxException
	 */
	public String computeMetrics(ModelTypingSpace familyTypingSpace, IProject project) throws Exception {
//		ConceptComparison conceptComparisonOperator = NamingConceptComparison.class.newInstance();
		ConceptComparison conceptComparisonOperator = DeepConceptComparison.class.newInstance();
//		MethodComparison methodComparisonOperator = NamingMethodComparison.class.newInstance();
//		MethodComparison methodComparisonOperator = SignatureMethodComparison.class.newInstance();
		MethodComparison methodComparisonOperator = SignatureAndSourceMethodComparison.getInstance();
		
		ArrayList<EPackage> ePackages = new ArrayList<EPackage>();
		ArrayList<Language> languages = new ArrayList<Language>();
		
		for (Element element : familyTypingSpace.getElements()) {
			if(element instanceof Language){
				Language language = (Language)element;
				languages.add(language);
				EPackage currentMetamodel = ModelUtils.loadEcoreResource(language.getSyntax().getEcoreUri());
				ePackages.add(currentMetamodel);
			}
		}
		
		String metrics = "Metrics calculated"; 
		
		List<ChartMetric> chartMetrics = new ArrayList<ChartMetric>();
		chartMetrics.add(new SizeOfCommonality());
		chartMetrics.add(new ProductRelatedReusability());
		chartMetrics.add(new IndividualizationRatio());
		chartMetrics.add(new PairwiseRelationshipRatio());
		
		String generalMetricsString = "";
		for (ChartMetric chartMetric : chartMetrics) {
			generalMetricsString += chartMetric.getVariablesDeclaration(languages, conceptComparisonOperator, methodComparisonOperator);
		}

		String generalMetricsWindowsString = "window.onload = function(){\n";
		for (ChartMetric chartMetric : chartMetrics) {
			generalMetricsWindowsString += chartMetric.getWindow(languages);
		}
		generalMetricsWindowsString += "};";
		
		File generalMetrics = new File(project.getLocation().toString() + "/lib/metrics.jsonp" );
		if(!generalMetrics.exists())
			generalMetrics.createNewFile();
		PrintWriter outMetrics = new PrintWriter( generalMetrics );
		outMetrics.print(generalMetricsString + "\n" + generalMetricsWindowsString);
		outMetrics.close();
		
		
		File syntacticVennData = new File(project.getLocation().toString() + "/libVenn/syntacticVennData.jsonp" );
		if(!syntacticVennData.exists())
			syntacticVennData.createNewFile();
		PrintWriter out = new PrintWriter( syntacticVennData );
        out.print(SyntacticVennDiagram.evaluate(languages, conceptComparisonOperator));
        out.close();
        
        File semanticVennData = new File(project.getLocation().toString() + "/libVenn/semanticVennData.jsonp" );
		if(!semanticVennData.exists())
			semanticVennData.createNewFile();
		PrintWriter outSemanticVennData = new PrintWriter( semanticVennData );
		outSemanticVennData.print(SemanticalVennDiagram.evaluate(languages, conceptComparisonOperator, methodComparisonOperator));
		outSemanticVennData.close();
		
		FamilysMetricManager familysMetric = new FamilysMetricManager(project);
		
		familysMetric.createReport1FamilysShape(languages);
		familysMetric.createReport2CostSaving(languages);
		familysMetric.createReport2CostSavingData(languages, conceptComparisonOperator, methodComparisonOperator);
		familysMetric.createReport3ReuseMetrics(languages);
		familysMetric.createDependenciesGraphData(languages, conceptComparisonOperator, methodComparisonOperator);
		familysMetric.createDependenciesGraph(languages);
		familysMetric.createFamilyMembershipGraphData(languages, conceptComparisonOperator, methodComparisonOperator);
		familysMetric.createFamilyMembershipGraph(languages);
		familysMetric.createTarjansGraphData(languages, conceptComparisonOperator, methodComparisonOperator);
		familysMetric.createTarjansGraph(languages);
		familysMetric.copyAnalysisSemanticsData(languages, conceptComparisonOperator, methodComparisonOperator);
		familysMetric.copyAnalysisSemantics(languages);
		
		ProjectManagementServices.refreshProject(project);
		System.out.println("SignatureAndSourceMethodComparison.getInstance().getAmountComputations(): " + SignatureAndSourceMethodComparison.getInstance().getAmountComputations());
		return metrics;
	}
}