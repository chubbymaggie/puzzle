package fr.inria.diverse.puzzle.metrics.evaluators.syntax;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.comparisonOperators.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.MethodComparison;
import fr.inria.diverse.k3.sle.common.tuples.ConceptMethodMemberVO;
import fr.inria.diverse.k3.sle.common.tuples.ConceptMethodMembersGroupVO;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class SemanticOverlapping {

	public static String evaluate(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator){
		Hashtable<String, Integer> membersSizeTable = new Hashtable<String, Integer>();
		ArrayList<ConceptMethodMemberVO> conceptMethodMemberList = FamiliesServices.getInstance().getConceptMethodMemberMappingList(languages);
		ArrayList<ConceptMethodMembersGroupVO> conceptMethodMemberGroupList = FamiliesServices.getInstance().getConceptMethodMemberGroupList(conceptMethodMemberList, conceptComparisonOperator, methodComparisonOperator);
		
		for (ConceptMethodMemberVO conceptMethodMemberVO : conceptMethodMemberList) {
			if(membersSizeTable.get(conceptMethodMemberVO.getMemberName()) == null)
				membersSizeTable.put(conceptMethodMemberVO.getMemberName(), 1);
			else{
				Integer currentValue = membersSizeTable.get(conceptMethodMemberVO.getMemberName());
				membersSizeTable.put(conceptMethodMemberVO.getMemberName(), currentValue + 1);
			}
		}
		
		String answer = "var semanticVennData = [";
		for (int i = 0; i < languages.size(); i++) {
			EPackage currentEPackage = MelangeServices.getEPackageFromLanguage(languages.get(i));
			answer += "{sets : [" + i + "], label : '" + currentEPackage.getName() + "', size : " + membersSizeTable.get(currentEPackage.getName()) + ",}";
			answer += ",\n              ";
		}
		
		boolean first = true;
		for (int i = 0; i < languages.size(); i++) {
			Language languageI = languages.get(i);
			for (int j = i + 1; j < languages.size(); j++) {
				Language languageJ = languages.get(j);
				if(!first) answer += ",\n              ";
				answer += "{sets : [" + i + "," + j + "], size:" + getIntersection(conceptMethodMemberGroupList, languageI, languageJ, methodComparisonOperator).size() + "}";
				first = false;
			}
		}
		
		answer += "];";
		
		return answer;
	}

	private static ArrayList<String> getIntersection(
			ArrayList<ConceptMethodMembersGroupVO> conceptMethodMemberGroupList, Language languageI, Language languageJ,
			MethodComparison methodComparisonOperator) {

		String languageIName = MelangeServices.getEPackageFromLanguage(languageI).getName();
		String languageJName = MelangeServices.getEPackageFromLanguage(languageJ).getName();
		ArrayList<String> answer = new ArrayList<String>();
		for (ConceptMethodMembersGroupVO conceptMethodMembersGroupVO : conceptMethodMemberGroupList) {
			boolean isInI = conceptMethodMembersGroupVO.getMemberGroup().contains(languageIName);
			boolean isInJ = conceptMethodMembersGroupVO.getMemberGroup().contains(languageJName);
			if(isInI && isInJ)
				answer.add(conceptMethodMembersGroupVO.getConcept().getSimpleName() + "." + conceptMethodMembersGroupVO.getMethod().getSimpleName());
		}
		return answer;
	}
}
