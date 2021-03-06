package fr.inria.diverse.puzzle.breaker.breakers;

import java.util.ArrayList;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGroup;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;

/**
 * Class implementing the default partition for the graph.
 * This default partition generates one module with all the vertex of the graph. 
 * 
 * @author David Mendez-Acuna
 *
 */
public class DefaultParition implements GraphPartition {

	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	@Override
	public void graphPartition(EcoreGraph graph,
			ArrayList<TupleMembersConcepts> membersConceptList,
			ConceptComparison conceptComparisonOperator) {
		graph.setGroups(new ArrayList<EcoreGroup>());
		EcoreGroup uniqueGroup = new EcoreGroup("UniqueGroup");
		uniqueGroup.getVertex().addAll(graph.getVertex());
		graph.getGroups().add(uniqueGroup);
	}
}