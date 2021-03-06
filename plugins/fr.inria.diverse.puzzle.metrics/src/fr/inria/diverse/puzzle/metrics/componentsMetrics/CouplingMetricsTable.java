package fr.inria.diverse.puzzle.metrics.componentsMetrics;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class CouplingMetricsTable implements LanguageProductLineChartMetric {

	@Override
	public String getVariablesDeclaration(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator, 
			MethodComparison methodComparisonOperator, 
			EcoreGraph modularizationGraph,
			DependencyGraph dependenciesGraph) throws Exception {
		
		String javaScriptData = "google.load('visualization', '1.1', {packages:['table']});\n";
		javaScriptData += "google.setOnLoadCallback(drawTable);\n\n";
		javaScriptData += "function drawTable() {\n";
		javaScriptData += "  var data = new google.visualization.DataTable();\n";
		javaScriptData += "    data.addColumn('string', 'Group Name');\n";
		javaScriptData += "    data.addColumn('string', 'Group Name');\n";
		javaScriptData += "    data.addColumn('number', 'Coupling');\n";
		javaScriptData += "    data.addRows([";
		
		SumCoupling sumCouplingMetric = new SumCoupling();
		int sum = 0;
		
		for (int i = 0; i < modularizationGraph.getGroups().size(); i++) {
			ArrayList<EcoreVertex> groupI = modularizationGraph.getGroups().get(i).getVertex();
			String groupIName = EcoreGraph.getLanguageModuleName(groupI);
			for (int j = i + 1; j < modularizationGraph.getGroups().size(); j++) {
				if(i!=j){
					ArrayList<EcoreVertex> groupJ = modularizationGraph.getGroups().get(j).getVertex();
					String groupJName = EcoreGraph.getLanguageModuleName(groupJ);
					int pairCoupling = sumCouplingMetric.getCouplingByGroupsPair(groupI, groupJ, modularizationGraph.getArcs());
					javaScriptData += "      ['" + groupIName + "', '" + groupJName + "', " + pairCoupling + "],\n";
					sum += pairCoupling;
				}
			}
		}
		javaScriptData += "      [' ', 'Coupling Sum', " + sum + "],\n";
		javaScriptData += "      [' ', 'Coupling Avg', " + (new AverageCoupling()).compute(languages, conceptComparisonOperator, modularizationGraph) + "]\n";
		
		javaScriptData += "  ]);\n\n";
		javaScriptData += "  var table = new google.visualization.Table(document.getElementById('table_div'));";
		javaScriptData += "  table.draw(data, {showRowNumber: false, width: '80%', height: '100%'});\n";
		javaScriptData += "}\n";
		
		return javaScriptData;
	}

	@Override
	public String getWindow(ArrayList<Language> languages) {
		// TODO Auto-generated method stub
		return null;
	}
}