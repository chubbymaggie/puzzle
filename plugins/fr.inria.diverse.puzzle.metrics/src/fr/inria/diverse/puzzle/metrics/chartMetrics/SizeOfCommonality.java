package fr.inria.diverse.puzzle.metrics.chartMetrics;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMethodMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMethodMembers;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.melange.metamodel.melange.Language;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountConstructs;
import fr.inria.diverse.puzzle.metrics.auxiliarMetrics.CountMethods;

/**
 * Chart metric for the size of (syntactic and semantic) commonality
 * @author David Mendez-Acuna
 */
public class SizeOfCommonality implements FamilyChartMetric {

	// ---------------------------------------------------
	// Methods
	// ---------------------------------------------------

	@Override
	public String getVariablesDeclaration(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, 
			MethodComparison methodComparisonOperator, GraphPartition graphPartition) throws Exception{
		String answer = "";
		int syntacticCore = evaluateForSyntax(languages, conceptComparisonOperator);
		int totalConstructs = CountConstructs.countFamilyConstructs(languages, conceptComparisonOperator);
		int syntacticDifferences = totalConstructs - syntacticCore;
		
		int semanticCore = evaluateForSemantics(languages, conceptComparisonOperator, methodComparisonOperator);
		int totalMethods = CountMethods.countFamilyMethods(languages, conceptComparisonOperator, methodComparisonOperator);
		int semanticDifferences = totalMethods - semanticCore;
		
		answer += "var pieDataSyntacticCommonality = [{\n";
		answer += "        value: " + syntacticCore + ",\n";
		answer += "        color:\"#FAAC58\",\n";
		answer += "        highlight: \"#F7BE81\",\n";
		answer += "        label: \"Commonalities\"\n";
		answer += "    },\n";
		answer += "    {\n";
		answer += "        value: " +  syntacticDifferences + ",\n";
		answer += "        color:\"#F5F6CE\",\n";
		answer += "        highlight: \"#FBFBEF\",\n";
		answer += "        label: \"Particularities\"\n";
		answer += "    }\n";
		answer += "];\n\n";
		
		answer += "var pieDataSemanticCommonality = [{\n";
		answer += "        value: " + semanticCore + ",\n";
		answer += "        color:\"#FAAC58\",\n";
		answer += "        highlight: \"#F7BE81\",\n";
		answer += "        label: \"Commonalities\"\n";
		answer += "    },\n";
		answer += "    {\n";
		answer += "        value: " + semanticDifferences + ",\n";
		answer += "        color:\"#F5F6CE\",\n";
		answer += "        highlight: \"#FBFBEF\",\n";
		answer += "        label: \"Particularities\"\n";
		answer += "    }\n";
		answer += "];\n\n";
		
		return answer;
	}
	
	@Override
	public String getWindow(ArrayList<Language> languages){
		String answer = "    var ctxSyntacticCommonalities = document.getElementById(\"chart-syntactic-commonalities\").getContext(\"2d\");\n";
		answer += "    window.myPieSyntacticCommonalities = new Chart(ctxSyntacticCommonalities).Pie(pieDataSyntacticCommonality);\n\n";
		answer += "    var ctxSemanticCommonalities = document.getElementById(\"chart-semantic-commonalities\").getContext(\"2d\");\n";
		answer += "    window.myPieSemanticCommonalities = new Chart(ctxSemanticCommonalities).Pie(pieDataSemanticCommonality);\n\n";
		return answer;
	}
	
	// ---------------------------------------------------
	// Auxiliar services
	// ---------------------------------------------------
	
	public static int evaluateForSyntax(ArrayList<Language> languages, ConceptComparison comparisonOperator) throws Exception{
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMemberGroupList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, comparisonOperator);
		int count = 0;
		for (TupleConceptMembers conceptMembersGroupVO : conceptMemberGroupList) {
			if(conceptMembersGroupVO.getMembers().size() == languages.size()){
				count++;
			}
		}
		return count;
	}
	
	public static int evaluateForSemantics(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator){
		ArrayList<TupleConceptMethodMember> conceptMethodMemberList = FamiliesServices.getInstance().getConceptMethodMemberMappingList(languages);
		ArrayList<TupleConceptMethodMembers> conceptMethodMemberGroupList = FamiliesServices.getInstance().getConceptMethodMemberGroupList(conceptMethodMemberList, conceptComparisonOperator, methodComparisonOperator);
		
		int count = 0;
		for (TupleConceptMethodMembers conceptMethodMembersGroupVO : conceptMethodMemberGroupList) {
			if(conceptMethodMembersGroupVO.getMembers().size() == languages.size()){
				count++;
			}
		}
		return count;
	}
}