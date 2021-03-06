package fr.inria.diverse.puzzle.metrics.chartMetrics;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.xtext.common.types.JvmOperation;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CollectConstructs;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CollectMethods;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountConstructs;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountConstructsOccurrences;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountMethods;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountMethodsOccurrences;

/**
 * Chart metric for the family's maintenance costs versus product line maintenance costs.
 * @author David Mendez-Acuna
 */
public class MaintananceCosts implements FamilyChartMetric {

	// ---------------------------------------------------
	// Methods
	// ---------------------------------------------------
	
	@Override
	public String getVariablesDeclaration(ArrayList<Language> languages,
			ConceptComparison conceptComparisonOperator,
			MethodComparison methodComparisonOperator, GraphPartition graphPartition) throws Exception {
		
		int[] constructsAmountsArray = this.getConstructsAmount(languages, conceptComparisonOperator);
		double pendiente = this.getAverageCostSyntax(languages, conceptComparisonOperator, methodComparisonOperator);
		int manHoursByConstruct = 2;
		
		String javaScriptData = "google.load('visualization', '1.1', {packages: ['line', 'corechart']});\n";
		javaScriptData += "google.setOnLoadCallback(drawChart);\n\n";
		javaScriptData += "    function drawChart() {\n";
		javaScriptData += "      var syntaxChart;\n";
		javaScriptData += "      var syntaxDiv = document.getElementById('chart-maitenance-costs-syntax');\n";
		javaScriptData += "      var data = new google.visualization.DataTable()\n";
		javaScriptData += "      data.addColumn('number', \"Amount of constructs\");\n";
		javaScriptData += "      data.addColumn('number', \"Family approach\");\n";
		javaScriptData += "      data.addColumn('number', \"Product line approach\");\n\n";
		javaScriptData += "      data.addRows([\n";
		
		boolean first = true;
		for (int i = 0; i < constructsAmountsArray.length; i++) {
			if(!first) javaScriptData += ", \n";
			javaScriptData += "        [" + constructsAmountsArray[i] + ",  " + constructsAmountsArray[i] * manHoursByConstruct * pendiente + ",  " + constructsAmountsArray[i] * manHoursByConstruct + "]";
			first = false;
		}
		
		javaScriptData += "      ]);\n\n";
		javaScriptData += "      var syntaxOptions = {\n";
		javaScriptData += "        title: 'Maintenance costs of the family of DSLs vs. maintenance costs of its corresponding language product line (abstract syntax)',\n";
		javaScriptData += "        titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "        width: 650,\n";
		javaScriptData += "        height: 280,\n";
		javaScriptData += "        series: {\n";
		javaScriptData += "          0: {targetAxisIndex: 0}\n";
		javaScriptData += "        },\n";
		javaScriptData += "        vAxes: {\n";
		javaScriptData += "          0: {title: 'Maintenance Costs (Man-Hour)',\n";
		javaScriptData += "              titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "              textStyle: {fontSize: 10, fontName: \"lucida sans unicode\", bold: true }\n";
		javaScriptData += "             }\n";
		javaScriptData += "        },\n";
		javaScriptData += "        hAxis: {\n";
		javaScriptData += "          title: 'Amount of Involved Constructs',\n";
		javaScriptData += "          titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "          textStyle: {fontSize: 10, fontName: \"lucida sans unicode\", bold: true },\n";
		javaScriptData += "          ticks: [";
		
		first = true;
		for (int i = 0; i < constructsAmountsArray.length; i++) {
			if(!first) javaScriptData += ", ";
			javaScriptData += constructsAmountsArray[i];
			first = false;
		}
		javaScriptData += "]\n";
		javaScriptData += "        },\n";
		javaScriptData += "        legend: { position: 'top',\n";
		javaScriptData += "      			  textStyle: {fontSize: 10, fontName: \"lucida sans unicode\" }\n";
		javaScriptData += "    			}\n";
		javaScriptData += "      	};\n";
		
		
		int[] methodsAmountsArray = this.getMethodsAmount(languages, conceptComparisonOperator, methodComparisonOperator);
		double pendienteMethods = this.getAverageCostSemantics(languages, conceptComparisonOperator, methodComparisonOperator);
		int manHoursByMethod = 2;
		
		javaScriptData += "      var semanticsChart;\n";
		javaScriptData += "      var semanticsDiv = document.getElementById('chart-maitenance-costs-semantics');\n";
		javaScriptData += "      var semanticsData = new google.visualization.DataTable()\n";
		javaScriptData += "      semanticsData.addColumn('number', \"Amount of methods\");\n";
		javaScriptData += "      semanticsData.addColumn('number', \"Family approach\");\n";
		javaScriptData += "      semanticsData.addColumn('number', \"Product line approach\");\n\n";
		javaScriptData += "      semanticsData.addRows([\n";
		
		for (int i = 0; i < methodsAmountsArray.length; i++) {
			javaScriptData += "        [" + methodsAmountsArray[i] + ",  " + methodsAmountsArray[i] * manHoursByMethod * pendienteMethods + ",  " + methodsAmountsArray[i] * manHoursByMethod + "],\n";
		}
		
		javaScriptData += "      ]);\n\n";
		javaScriptData += "      var semanticsOptions = {\n";
		javaScriptData += "        title: 'Maintenance costs of the family of DSLs vs. maintenance costs of its corresponding language product line (semantics)',\n";
		javaScriptData += "        titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "        width: 650,\n";
		javaScriptData += "        height: 280,\n";
		javaScriptData += "        series: {\n";
		javaScriptData += "          0: {targetAxisIndex: 0}\n";
		javaScriptData += "        },\n";
		javaScriptData += "        vAxes: {\n";
		javaScriptData += "          0: {title: 'Maintenance Costs (Man-Hour)',\n";
		javaScriptData += "              titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "              textStyle: {fontSize: 10, fontName: \"lucida sans unicode\", bold: true }\n";
		javaScriptData += "             }\n";
		javaScriptData += "        },\n";
		javaScriptData += "        hAxis: {\n";
		javaScriptData += "          title: 'Amount of Involved Domain-Specific Actions (DSAs)',\n";
		javaScriptData += "          titleTextStyle: {fontSize: 10, fontName: \"lucida sans unicode\" },\n";
		javaScriptData += "          textStyle: {fontSize: 10, fontName: \"lucida sans unicode\", bold: true },\n";
		javaScriptData += "          ticks: [";
		
		first = true;
		for (int i = 0; i < methodsAmountsArray.length; i++) {
			if(!first) javaScriptData += ", ";
			javaScriptData += methodsAmountsArray[i];
			first = false;
		}
		javaScriptData += "]\n";
		javaScriptData += "        },\n";
		javaScriptData += "        legend: { position: 'top',\n";
		javaScriptData += "      			  textStyle: {fontSize: 10, fontName: \"lucida sans unicode\" }\n";
		javaScriptData += "    			}\n";
		javaScriptData += "      	};\n";
		
		javaScriptData += "      syntaxChart = new google.visualization.LineChart(syntaxDiv);\n";
		javaScriptData += "      syntaxChart.draw(data, syntaxOptions);\n";
		
		javaScriptData += "      syntaxChart = new google.visualization.LineChart(semanticsDiv);\n";
		javaScriptData += "      syntaxChart.draw(semanticsData, semanticsOptions);\n";
		javaScriptData += "    }\n";
		
		return javaScriptData;
	}

	@Override
	public String getWindow(ArrayList<Language> languages) {
		return "";
	}
	
	// ---------------------------------------------------
	// Auxiliar services
	// ---------------------------------------------------
	
	private int[] getConstructsAmount(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator) throws Exception {
		int[] answer = new int[10];
		int constructsAmount = CountConstructs.countFamilyConstructs(languages, conceptComparisonOperator);
		int interval = constructsAmount/9;
		for (int i = 0; i < 10; i++) {
			answer[i] += interval * (i);
		}
		return answer;
	}
	
	private int[] getMethodsAmount(ArrayList<Language> languages,
			ConceptComparison conceptComparisonOperator,
			MethodComparison methodComparisonOperator) {
		int[] answer = new int[10];
		int constructsAmount = CountMethods.countFamilyMethods(languages, conceptComparisonOperator, methodComparisonOperator);
		int interval = constructsAmount/9;
		for (int i = 0; i < 10; i++) {
			answer[i] += interval * (i);
		}
		return answer;
	}
	
	public double getAverageCostSyntax(ArrayList<Language> languages,
			ConceptComparison conceptComparisonOperator,
			MethodComparison methodComparisonOperator) throws Exception{
		double totalCost = 0;
		List<EClassifier> constructs = CollectConstructs.collectFamilyConstructs(languages, conceptComparisonOperator);
		for (EClassifier eClassifier : constructs) {
			totalCost += CountConstructsOccurrences.intCountConstructOccurrences(languages, conceptComparisonOperator, eClassifier);
		}
		if(constructs.size() == 0)
			return 0;
		else
			return totalCost/constructs.size();
	}
	
	private double getAverageCostSemantics(ArrayList<Language> languages,
			ConceptComparison conceptComparisonOperator,
			MethodComparison methodComparisonOperator) throws Exception {
		double totalCost = 0;
		List<JvmOperation> methods = CollectMethods.collectFamilyMethods(languages, conceptComparisonOperator, methodComparisonOperator);
		for (JvmOperation jvmOperation : methods) {
			totalCost += CountMethodsOccurrences.countMethodsOccurrences(languages, conceptComparisonOperator, methodComparisonOperator, jvmOperation);
		}
		if(methods.size() == 0)
			return 0;
		else
			return totalCost/methods.size();
	}
}