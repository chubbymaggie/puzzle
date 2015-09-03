package fr.inria.diverse.puzzle.metrics.evaluators.syntax;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.utils.EcoreQueries;

public class RelationshipRatio {

	public static String evaluate(ArrayList<EPackage> ePackages){
		String result = "";
		
		for (EPackage ePackageI : ePackages) {
			for (EPackage ePackageJ : ePackages) {
				if(!ePackageI.getName().equals(ePackageJ.getName())){
					double currentValue = ((double)EcoreQueries.getIntersection(ePackageI, ePackageJ).size()) / ((double)EcoreQueries.getUnion(ePackageI, ePackageJ).size());
					
					result += "  - " + ePackageI.getName() + " vs " + ePackageJ.getName() + ": " + currentValue + " (" + EcoreQueries.getIntersection(ePackageI, ePackageJ).size() + ", "+ EcoreQueries.getUnion(ePackageI, ePackageJ).size() + ")" + "\n";
				}
			}
		}
		
		return result;
	}
}