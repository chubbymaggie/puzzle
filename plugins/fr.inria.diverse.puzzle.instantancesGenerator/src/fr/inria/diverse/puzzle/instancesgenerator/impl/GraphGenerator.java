package fr.inria.diverse.puzzle.instancesgenerator.impl;

import java.util.Random;

import fr.inria.diverse.k3.sle.common.graphs.DependencyArc;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.graphs.DependencyVertex;

/**
 * Random generator of acyclic directed graph.
 * The algorithm is the one proposed by Malecon et al. [1]
 * 
 * [1]: G. Melançon, I. Dutour, M. Bousquet-Mélou, Random Generation of Directed Acyclic Graphs, 
 * Electronic Notes in Discrete Mathematics, Volume 10, November 2001, Pages 202-207, 
 * ISSN 1571-0653, http://dx.doi.org/10.1016/S1571-0653(04)00394-4.
 * 
 * @author David Mendez-Acuna
 *
 */
public class GraphGenerator {

	// -------------------------------------------------------
	// Methods
	// -------------------------------------------------------
	
	/**
	 * Generates a random acyclic directed graph by using the given seed. 
	 * @param size. Number of vertex of the desired graph.
	 * @param seed. Seed for generation of the random positions of the graph. 
	 * @return
	 */
	public static DependencyGraph generateGraph(String namePrefix, int size, long seed, int chainLenght){
		Random generator = new Random(seed);
		
		// Creating an empty graph (without arcs)
		DependencyGraph graph = new DependencyGraph();
		for (int i = 1; i <= size; i++) {
			DependencyVertex vertex = new DependencyVertex(namePrefix + Integer.toString(i));
			graph.getVertex().add(vertex);
		}
		
		// Creating the arcs between vertex using a Markov chain. 
		int iterations = chainLenght;
		
		if(chainLenght == -1)
			iterations = size * 3;
		
		while(iterations > 0){
			int i = 0 + (int)(generator.nextDouble() * (size));
			int j = 0 + (int)(generator.nextDouble() * (size));
			
			DependencyVertex vertexI = (DependencyVertex) graph.getVertex().get(i);
			DependencyVertex vertexJ = (DependencyVertex) graph.getVertex().get(j);
			
			
			if(graph.thereIsArc(vertexI, vertexJ)){
				// (a) If the position (i,j) corresponds to an arc e in Xt, then Xt+1 = Xt\e
				DependencyArc oldArc = (DependencyArc) graph.getArc(vertexI, vertexJ);
				oldArc.getFrom().getOutgoingArcs().remove(oldArc);
				oldArc.getTo().getIncomingArcs().remove(oldArc);
				graph.getArcs().remove(oldArc);
			}else{
				// (b) If the position (i,j) does not correspond to an arc in Xt, then Xt+1 is obtained from Xt
				//     by adding this arc while avoiding loops. 
				DependencyArc arc = new DependencyArc(vertexI, vertexJ);
				graph.getArcs().add(arc);
				
				if(graph.thereIsLoop()){
					arc.getFrom().getOutgoingArcs().remove(arc);
					arc.getTo().getIncomingArcs().remove(arc);
					graph.getArcs().remove(arc);
				}
			}
			iterations--;
		}
		return graph;
	}
}
