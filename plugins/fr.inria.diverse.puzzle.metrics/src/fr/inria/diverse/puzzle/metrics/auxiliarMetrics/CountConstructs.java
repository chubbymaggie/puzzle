package fr.inria.diverse.puzzle.metrics.auxiliarMetrics;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EPackage;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMember;
import fr.inria.diverse.k3.sle.common.tuples.TupleConceptMembers;
import fr.inria.diverse.k3.sle.common.utils.FamiliesServices;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Constructs counter
 * @author David Mendez-Acuna
 */
public class CountConstructs {

	public static int countFamilyConstructs(ArrayList<Language> languages, ConceptComparison comparisonOperator) throws Exception{
		ArrayList<TupleConceptMember> conceptMemberList = FamiliesServices.getInstance().getConceptMemberMappingList(languages);
		ArrayList<TupleConceptMembers> conceptMemberGroupList = FamiliesServices.getInstance().getConceptMemberGroupList(conceptMemberList, comparisonOperator);
		return conceptMemberGroupList.size();
	}
	
	public static int countLanguageConstructs(EPackage ePackage){
		int count = ePackage.getEClassifiers().size();
		for (EPackage eSubPackage : ePackage.getESubpackages()) {
			count += countLanguageConstructs(eSubPackage);
		}
		return count;
	}
}
