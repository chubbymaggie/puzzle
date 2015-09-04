package fr.inria.diverse.k3.sle.common.graphsOperators;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;

public class DefaultParition implements GraphPartition {

	@Override
	public void graphPartition(EcoreGraph graph,
			ArrayList<TupleMembersConcepts> membersConceptList,
			ConceptComparison conceptComparisonOperator) {
		graph.setGroups(new ArrayList<ArrayList<EcoreVertex>>());
		ArrayList<EcoreVertex> uniqueGroup = new ArrayList<EcoreVertex>();
		uniqueGroup.addAll(graph.getVertex());
		graph.getGroups().add(uniqueGroup);
	}
}