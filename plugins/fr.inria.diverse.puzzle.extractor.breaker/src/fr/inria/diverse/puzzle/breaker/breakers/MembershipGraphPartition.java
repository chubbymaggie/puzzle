package fr.inria.diverse.puzzle.breaker.breakers;

import java.util.ArrayList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.commands.GraphPartition;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;
import fr.inria.diverse.k3.sle.common.graphs.EcoreGroup;
import fr.inria.diverse.k3.sle.common.graphs.EcoreVertex;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;

/**
 * Class implementing a graph partition that uses family membership as partition criteria. 
 * A module is generated by each parents configuration.
 * 
 * @author David Mendez-Acuna
 *
 */
public class MembershipGraphPartition implements GraphPartition {

	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	@Override
	public void graphPartition(EcoreGraph graph, ArrayList<TupleMembersConcepts> membersConceptList, ConceptComparison conceptComparisonOperator) {
		graph.setGroups(new ArrayList<EcoreGroup>());
		for (TupleMembersConcepts membersGroupVsConceptVO : membersConceptList) {
			EcoreGroup currentGroup = new EcoreGroup("");
			for (EClassifier currentConcept : membersGroupVsConceptVO.getConcepts()) {
				currentGroup.getVertex().add(graph.getNodeByConceptComparisonOperator(graph, currentConcept, conceptComparisonOperator));
			}
			this.computeRequiredVertexSet(membersGroupVsConceptVO.getConcepts(), currentGroup, conceptComparisonOperator);
			currentGroup.setName(EcoreGraph.getLanguageModuleName(currentGroup.getVertex()));
			graph.getGroups().add(currentGroup);
		}
	}

	/**
	 * Finds and add references the required vertexes in the given group. 
	 * @param concepts
	 * @param group
	 * @param conceptComparisonOperator
	 * 
	 * TODO: This method might be required by other graph partitioning algorithms... 
	 * 		 Is GraphParition an abstract class instead of an interface?
	 * 
	 * TODO: The algorithm is creating one new vertex for each reference! We need to avoid repeated ones. 
	 */
	private void computeRequiredVertexSet(ArrayList<EClassifier> concepts, EcoreGroup group, ConceptComparison conceptComparisonOperator) {
		ArrayList<EClassifier> consideredTypes = new ArrayList<EClassifier>();
		for (EClassifier eClassifier : concepts) {
			if(eClassifier instanceof EClass){
				EClass eClass = (EClass) eClassifier;
				for(EStructuralFeature eStructuralFeature : eClass.getEStructuralFeatures()){
					if(eStructuralFeature instanceof EReference){
						EcoreVertex vertex = group.findVertexByEcoreReference(eStructuralFeature.getEType(), conceptComparisonOperator);
						if(vertex == null && eStructuralFeature.getEType() != null && !consideredTypes.contains(eStructuralFeature.getEType())){
							vertex = new EcoreVertex(eStructuralFeature.getEType().getName(), eStructuralFeature.getEType());
							consideredTypes.add(eStructuralFeature.getEType());
							group.getRequiredVertex().add(vertex);
						}
					}
				}
				
				for(EClass superType : ((EClass) eClassifier).getESuperTypes()){
					EcoreVertex vertex = group.findVertexByEcoreReference(superType, conceptComparisonOperator);
					if(vertex == null && superType != null && !consideredTypes.contains(superType)){
						vertex = new EcoreVertex(superType.getName(), superType);
						consideredTypes.add(superType);
						group.getRequiredVertex().add(vertex);
					}
				}
			}
		}
	}
}