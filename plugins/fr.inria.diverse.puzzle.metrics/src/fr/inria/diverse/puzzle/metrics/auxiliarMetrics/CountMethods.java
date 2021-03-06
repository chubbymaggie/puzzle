package fr.inria.diverse.puzzle.metrics.auxiliarMetrics;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmOperation;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMethodMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMethodMembers;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.melange.metamodel.melange.Aspect;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Methods counter
 * @author David Mendez-Acuna
 */
public class CountMethods {

	public static int countFamilyMethods(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, MethodComparison methodComparisonOperator ){
		ArrayList<TupleConceptMethodMember> conceptMethodMemberList = FamiliesServices.getInstance().getConceptMethodMemberMappingList(languages);
		ArrayList<TupleConceptMethodMembers> conceptMethodMemberGroupList = FamiliesServices.getInstance().getConceptMethodMemberGroupList(conceptMethodMemberList, conceptComparisonOperator, methodComparisonOperator);
		return conceptMethodMemberGroupList.size();
	}
	
	public static double countLanguageMethods(Language language){
		double count = 0;
		for (Aspect aspect : language.getSemantics()) {
			for (EObject aspectContent : aspect.getAspectTypeRef().getType().eContents()) {
				if(aspectContent instanceof JvmOperation)
					count ++;
			}
		}
		return count;
	}
}
