package fr.inria.diverse.puzzle.metrics.specialCharts;

import java.util.ArrayList;

import fr.inria.diverse.graph.Arc;
import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.melange.metamodel.melange.Language;

/**
 * Prints the family membership graph.
 * @author David Mendez-Acuna
 *
 */
public class ExternalDependenciesGraph implements SpecialProductLineSyntacticChart{

	// ------------------------------------------------------
	// Methods
	// ------------------------------------------------------
	
	@Override
	public String getVariablesDeclaration(ArrayList<Language> languages, 
			ConceptComparison conceptComparisonOperator,
			EcoreGraph modularizationGraph,
			DependencyGraph dependenciesGraph) throws Exception{
		
		String answer = "var G = new jsnx.DiGraph();\n";
		
		boolean first = true;
		int i = 0;
		for (Vertex currentVertex : dependenciesGraph.getVertex()) {
			answer += "G.addNodesFrom([" + "\"" + currentVertex.getIdentifier() + "\"" + "], {group: " + i + " });\n";
			i++;
		}
			
		answer += "\n";
		if(dependenciesGraph.getArcs().size() > 0){
			answer += "G.addEdgesFrom([";
			
			first = true;
			for (Arc arc : dependenciesGraph.getArcs()) {
				if(!first) answer += ",";
				answer += "[\"" + arc.getFrom().getIdentifier() + "\",\""+ arc.getTo().getIdentifier() +"\"]";
				first = false;
			}
			answer += "]);\n";
		}
		return answer;
	}
}