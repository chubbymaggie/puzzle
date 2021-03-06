package fr.inria.diverse.puzzle.metrics.componentsMetrics;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.melange.metamodel.melange.Language;

public class AverageCoupling {

	public double compute(ArrayList<Language> languages, ConceptComparison conceptComparisonOperator, EcoreGraph modularizationGraph) throws Exception{
		SumCoupling sumOperator = new SumCoupling();
		int sum = sumOperator.compute(languages, conceptComparisonOperator, modularizationGraph);
		
		double aveCoupling = sum / modularizationGraph.getGroups().size();
		return aveCoupling;
	}	
}
