package fr.inria.diverse.puzzle.metrics.componentsMetrics;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.metrics.chartMetrics.ChartMetric;

public class CouplingMetricsTable implements ChartMetric{

	@Override
	public String getVariablesDeclaration(ArrayList<Language> languages,
			ConceptComparison conceptComparisonOperator,
			MethodComparison methodComparisonOperator,
			GraphPartition graphPartition) throws Exception {
		
		String javaScriptData = "google.load('visualization', '1.1', {packages:['table']});\n";
		javaScriptData += "google.setOnLoadCallback(drawTable);\n\n";
		javaScriptData += "function drawTable() {\n";
		javaScriptData += "  var data = new google.visualization.DataTable();\n";
		javaScriptData += "    data.addColumn('string', 'Group Name');\n";
		javaScriptData += "    data.addColumn('string', 'Group Name');\n";
		javaScriptData += "    data.addColumn('number', 'Coupling');\n";
		javaScriptData += "    data.addRows([";
		
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMembersList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, conceptComparisonOperator);
		ArrayList<TupleMembersConcepts> membersConceptList = FamiliesServices.getInstance().getMembersGroupVsConceptVOList(conceptMembersList);
		EcoreGraph dependenciesGraph = new EcoreGraph(membersConceptList, conceptComparisonOperator);
		graphPartition.graphPartition(dependenciesGraph, membersConceptList, conceptComparisonOperator);
		
		SumCoupling sumCouplingMetric = new SumCoupling();
		int sum = 0;
		
		for (int i = 0; i < dependenciesGraph.getGroups().size(); i++) {
			ArrayList<EcoreVertex> groupI = dependenciesGraph.getGroups().get(i);
			String groupIName = EcoreGraph.getLanguageModuleName(groupI);
			for (int j = i + 1; j < dependenciesGraph.getGroups().size(); j++) {
				if(i!=j){
					ArrayList<EcoreVertex> groupJ = dependenciesGraph.getGroups().get(j);
					String groupJName = EcoreGraph.getLanguageModuleName(groupJ);
					int pairCoupling = sumCouplingMetric.getCouplingByGroupsPair(groupI, groupJ, dependenciesGraph.getArcs());
					javaScriptData += "      ['" + groupIName + "', '" + groupJName + "', " + pairCoupling + "],\n";
					sum += pairCoupling;
				}
			}
		}
		javaScriptData += "      [' ', 'Coupling Sum', " + sum + "],\n";
		javaScriptData += "      [' ', 'Coupling Avg', " + (new AverageCoupling()).compute(languages, conceptComparisonOperator, graphPartition) + "]\n";
		
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