package fr.inria.diverse.puzzle.metrics.auxiliarMetrics;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.common.types.JvmOperation;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.MethodComparison;
import fr.inria.diverse.melange.metamodel.melange.Aspect;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class CountMethodsOccurrences {

	public static int countMethodsOccurrences(ArrayList<Language> languages, ConceptComparison comparisonOperator, MethodComparison methodComparisonOperator, JvmOperation method) throws Exception{
		int occurrences = 0;
		for (Language language : languages) {
			for (Aspect aspect : language.getSemantics()) {
				for (EObject eObject : aspect.getAspectTypeRef().getType().eContents()) {
					if(eObject instanceof JvmOperation){
						if(methodComparisonOperator.equal(method, (JvmOperation)eObject))
							occurrences++;
					}
				}
			}
		}
		return occurrences;
	}
}
