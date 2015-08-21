package fr.inria.diverse.puzzle.metrics.evaluators.syntax;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.comparisonOperators.ConceptComparison;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.k3.sle.common.utils.MelangeServices;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class ReusabilityBenefit {

	public static String evaluate(ArrayList<Language> languages, ConceptComparison comparisonOperator) throws Exception{
		ArrayList<EPackage> ePackages = MelangeServices.getEPackagesByALanguagesList(languages);
		double SoC = SizeOfCommonality.evaluateForSyntax(languages, comparisonOperator);
		String result = "";
		
		for (EPackage ePackageI : ePackages) {
			for (EPackage ePackageJ : ePackages) {
				if(!ePackageI.getName().equals(ePackageJ.getName())){
					double currentValue = SoC / FamiliesServices.getIntersection(ePackageI, ePackageJ, comparisonOperator).size();
					
					result += "  - " + ePackageI.getName() + " vs " + ePackageJ.getName() + ": " + currentValue + "\n";
				}
			}
		}
		return result;
	}
}