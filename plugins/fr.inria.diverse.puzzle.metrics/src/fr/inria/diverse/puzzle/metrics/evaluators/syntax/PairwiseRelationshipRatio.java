package fr.inria.diverse.puzzle.metrics.evaluators.syntax;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.comparisonOperators.ConceptComparison;
import fr.inria.diverse.k3.sle.common.comparisonOperators.MethodComparison;
import fr.inria.diverse.k3.sle.common.tuples.ConceptMethodMemberVO;
import fr.inria.diverse.k3.sle.common.tuples.ConceptMethodMembersGroupVO;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class PairwiseRelationshipRatio {

	public static String evaluate(ArrayList<Language> languages, ConceptComparison comparisonOperator){
		ArrayList<EPackage> ePackages = MelangeServices.getEPackagesByALanguagesList(languages);
		String result = "";
		
		for (EPackage ePackageI : ePackages) {
			for (EPackage ePackageJ : ePackages) {
				if(!ePackageI.getName().equals(ePackageJ.getName())){
					double currentValue = ((double) FamiliesServices.getIntersection(ePackageI, ePackageJ, comparisonOperator).size()) / ((double) FamiliesServices.getUnion(ePackageI, ePackageJ).size());
					
					result += "  - " + ePackageI.getName() + " vs " + ePackageJ.getName() + ": " + currentValue + " (" + FamiliesServices.getIntersection(ePackageI, ePackageJ, comparisonOperator).size() + ", "+ FamiliesServices.getUnion(ePackageI, ePackageJ).size() + ")" + "\n";
				}
			}
		}
		return result;
	}
	
	public static String getVariablesDeclaration(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator){
		String answer = "";
		ArrayList<ConceptMethodMemberVO> conceptMethodMemberList = FamiliesServices.getInstance().getConceptMethodMemberMappingList(languages);
		ArrayList<ConceptMethodMembersGroupVO> conceptMethodMemberGroupList = FamiliesServices.getInstance().getConceptMethodMemberGroupList(conceptMethodMemberList, conceptComparisonOperator, methodComparisonOperator);
		
		for (Language languageI : languages) {
			EPackage ePackageI = MelangeServices.getEPackageFromLanguage(languageI);
			answer += "var barRelationshipRatio" + ePackageI.getName() + " = {\n";
			
			String labels = "";
			String dataForSyntax = "";
			boolean first = true;
			for (Language languageJ : languages) {
				
				EPackage ePackageJ = MelangeServices.getEPackageFromLanguage(languageJ);
				if(!ePackageI.getName().equals(ePackageJ.getName())){
					double currentValue = (((double)FamiliesServices.getIntersection(ePackageI, ePackageJ, conceptComparisonOperator).size()) / ((double)FamiliesServices.getUnion(ePackageI, ePackageJ).size()))*100;
					
					if(!first) labels += ",";
					labels += "\"" + ePackageJ.getName() + "\"";
					
					if(!first){
						dataForSyntax +=  ",";
					}
					dataForSyntax += currentValue;
					first = false;
					
				}
			}
			
			String dataForSemantics = "";
			first = true;
			for (Language languageJ : languages) {
				EPackage ePackageJ = MelangeServices.getEPackageFromLanguage(languageJ);
				if(!ePackageI.getName().equals(ePackageJ.getName())){
					double currentValue = (((double)getIntersection(conceptMethodMemberGroupList, languageI, languageJ, methodComparisonOperator).size()) / ((double)FamiliesServices.getUnion(ePackageI, ePackageJ).size()))*100;
					
					if(!first){
						dataForSemantics +=  ",";
					}
					dataForSemantics += currentValue;
					first = false;
					
				}
			}
			
			answer += "   labels : [" + labels + "],\n";
			answer += "   datasets : [\n";
			answer += "     {\n";
			answer += "        fillColor : \"rgba(220,220,220,0.5)\",\n";
			answer += "        strokeColor : \"rgba(220,220,220,0.8)\",\n";
			answer += "        highlightFill: \"rgba(220,220,220,0.75)\",\n";
			answer += "        highlightStroke: \"rgba(220,220,220,1)\",\n";
			answer += "        data : [" + dataForSyntax + "]\n";
			answer += "      },\n";
			answer += "      {\n";
			answer += "        fillColor : \"rgba(151,187,205,0.5)\",\n";
			answer += "        strokeColor : \"rgba(151,187,205,0.8)\",\n";
			answer += "        highlightFill : \"rgba(151,187,205,0.75)\",\n";
			answer += "        highlightStroke : \"rgba(151,187,205,1)\",\n";
			answer += "        data : [" + dataForSemantics + "]\n"; 
			answer += "      }\n";
			answer += "    ]\n";
			answer += "};\n";
		}
		
		return answer;
	}
	
	public static String getWindow(ArrayList<Language> languages){
		ArrayList<EPackage> ePackages = MelangeServices.getEPackagesByALanguagesList(languages);
		String answer = "";
		for (EPackage ePackageI : ePackages) {
			answer += "    var ctxRelationshipRatio" + ePackageI.getName() + " = document.getElementById(\"pie-relationship-ratio-" + ePackageI.getName() + "\").getContext(\"2d\");\n";
			answer += "    window.window.window.myBarRelationshipRatio" + ePackageI.getName() + " = new Chart(ctxRelationshipRatio" + ePackageI.getName() + ").Bar(barRelationshipRatio" + ePackageI.getName() + ", {\n";
			answer += "       responsive : false\n";
			answer += "    });\n\n";
		}
		return answer;
	}
	
	public static String getTables(ArrayList<Language> languages){
		ArrayList<EPackage> ePackages = MelangeServices.getEPackagesByALanguagesList(languages);
		String answer = "";
		char index = 'a';
		for (EPackage ePackageI : ePackages) {
			answer += "                <table align=\"center\" border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"width:500px;\">\n";
			answer += "                    <tbody>\n";
			answer += "                        <tr>\n";
			answer += "                            <td style=\"text-align: center;\">\n";
			answer += "                                <div id=\"canvas-relationship-ratio-" + ePackageI.getName() + "\" width=\"150\" height=\"150\">\n";
			answer += "                                    <canvas id=\"pie-relationship-ratio-" + ePackageI.getName() + "\" width=\"450\" height=\"200\"/>\n";
			answer += "                                </div>\n";
			answer += "                            </td>\n";
			answer += "                        </tr>\n";
			answer += "                        <tr>\n";
			answer += "                            <td style=\"text-align: center;\">\n";
			answer += "                                <span style=\"font-family:lucida sans unicode,lucida grande,sans-serif;font-size:11px;\">\n";
			answer += "                                    <strong>Figure 7" + index + ".</strong></br>\n";
			answer += "                                    Pair-wise Relationship Ratio for " + ePackageI.getName() + "</br>\n";
			answer += "                                    ----\n";
			answer += "                                </span>\n";
			answer += "                            </td>\n";
			answer += "                        </tr>\n";
			answer += "                    </tbody>\n";
			answer += "                </table>\n";
			
			index++;
		}
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
