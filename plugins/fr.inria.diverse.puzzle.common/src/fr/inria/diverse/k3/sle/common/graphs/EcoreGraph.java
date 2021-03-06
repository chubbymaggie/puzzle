package fr.inria.diverse.k3.sle.common.graphs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import fr.inria.diverse.graph.Vertex;
import fr.inria.diverse.k3.sle.common.commands.ConceptComparison;
import fr.inria.diverse.k3.sle.common.tuples.TupleMembersConcepts;

/**
 * Class that implements the services of an ecore graph.
 * 
 * @author David Mendez-Acuna
 *
 */
public class EcoreGraph {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private ArrayList<EcoreVertex> vertex;
	private ArrayList<EcoreArc> arcs;
	private ArrayList<EcoreGroup> groups;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	/**
	 * Constructor by default. Returns an emtpy graph. 
	 */
	public EcoreGraph(){
		this.vertex = new ArrayList<EcoreVertex>();
		this.arcs = new ArrayList<EcoreArc>();
		this.groups = new ArrayList<EcoreGroup>();
	}
	
	/**
	 * Constructor that builds a dependencies graph from a list of Concept-Members tuple
	 */
	public EcoreGraph(ArrayList<TupleMembersConcepts> membersConceptList, ConceptComparison conceptComparisonOperator){
		this.vertex = new ArrayList<EcoreVertex>();
		this.arcs = new ArrayList<EcoreArc>();
		this.groups = new ArrayList<EcoreGroup>();
		this.computeDependenciesGraph(membersConceptList, conceptComparisonOperator);
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------

	public ArrayList<EcoreVertex> getVertex() {
		return vertex;
	}

	public ArrayList<EcoreArc> getArcs() {
		return arcs;
	}
	
	public ArrayList<EcoreGroup> getGroups() {
		return groups;
	}
	
	public void setGroups(ArrayList<EcoreGroup> groups) {
		this.groups = groups;
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------

	/**
	 * Computes the dependencies graph from a list of tuples Concept-Members
	 * @param conceptMembersList
	 * @return
	 */
	public void computeDependenciesGraph(ArrayList<TupleMembersConcepts> membersConceptList, ConceptComparison conceptComparisonOperator){
		for (TupleMembersConcepts conceptMembersTuple : membersConceptList) {
			for (EClassifier currentClassifier : conceptMembersTuple.getConcepts()) {
				EcoreVertex node = new EcoreVertex(currentClassifier.getName() + ": " + conceptMembersTuple.getMembersString(), currentClassifier);
				this.getVertex().add(node);
			}
		}
		
		// Adding one arc for each reference
		for (EcoreVertex node : this.getVertex()) {
			EClassifier currentClassifier = node.getClassifier();
			
			if(currentClassifier instanceof EClass){
				EClass currentEClass = (EClass) currentClassifier;
				for (EStructuralFeature structuralFeature : currentEClass.getEStructuralFeatures()) {
					if(structuralFeature instanceof EReference){
						EReference currentEReference = (EReference) structuralFeature;
						EcoreVertex toNode = getNodeByConceptComparisonOperator(this, currentEReference.getEType(), conceptComparisonOperator);
						if(toNode != null){
							EcoreArc arc = new EcoreArc(node, toNode);
							
							if(!node.getOutgoingArcs().contains(arc))
								node.getOutgoingArcs().add(arc);
							
							if(!toNode.getIncomingArcs().contains(arc))
								toNode.getIncomingArcs().add(arc);
							
							this.getArcs().add(arc);
						}
					}
					
					if(structuralFeature instanceof EAttribute){
						EAttribute currentEAttribute = (EAttribute) structuralFeature;
						if(currentEAttribute.getEType() instanceof EEnum){
							EcoreVertex toNode = getNodeByConceptComparisonOperator(this, currentEAttribute.getEType(), conceptComparisonOperator);
							if(toNode != null){
								EcoreArc arc = new EcoreArc(node, toNode);
								
								if(!node.getOutgoingArcs().contains(arc))
									node.getOutgoingArcs().add(arc);
								
								if(!toNode.getIncomingArcs().contains(arc))
									toNode.getIncomingArcs().add(arc);
								
								this.getArcs().add(arc);
							}
						}
					}
				}
			}
		}
		
		// Adding one arc for each inheritance
		for (EcoreVertex node : this.getVertex()) {
			EClassifier currentClassifier = node.getClassifier();
			
			if(currentClassifier instanceof EClass){
				EClass currentEClass = (EClass) currentClassifier;
				for (EClass superClass : currentEClass.getESuperTypes()) {
					EcoreVertex toNode = getNodeByConceptComparisonOperator(this, superClass, conceptComparisonOperator);
					if(toNode != null){
						EcoreArc arc = new EcoreArc(node, toNode);
						
						if(!node.getOutgoingArcs().contains(arc))
							node.getOutgoingArcs().add(arc);
						
						if(!toNode.getIncomingArcs().contains(arc))
							toNode.getIncomingArcs().add(arc);
						
						this.getArcs().add(arc);
					}
				}
			}
		}
	}
	
	/**
	 * Finds a node by using the concept comparison operator defined in the synthesis properties. 
	 * @param graph
	 * @param eType
	 * @param conceptComparisonOperator
	 * @return
	 */
	public EcoreVertex getNodeByConceptComparisonOperator(EcoreGraph graph,
			EClassifier eType, ConceptComparison conceptComparisonOperator) {
		for (EcoreVertex node : graph.getVertex()) {
			if(conceptComparisonOperator.equals(node.getClassifier(),eType))
				return node;
		} return null;
	}

	/**
	 * Returns a node by its name in the graph in the paramter
	 * @param graph
	 * @param nodeName
	 * @return
	 */
	public EcoreVertex getNodeByName(EcoreGraph graph, String nodeName){
		for (EcoreVertex node : graph.getVertex()) {
			if(node.getClassifier().getName().equals(nodeName))
				return node;
		} return null;
	}
	
	/**
	 * Returns a node by its name in the graph in the paramter
	 * @param graph
	 * @param nodeName
	 * @return
	 */
	public EcoreVertex getNodeByEClassifier(EcoreGraph graph, EClassifier eClassifier){
		for (EcoreVertex node : graph.getVertex()) {
			if(node.getClassifier().equals(eClassifier))
				return node;
		} return null;
	}
	
	/**
	 * Returns a node by its string representation in the graph in the paramter
	 * @param graph
	 * @param nodeName
	 * @return
	 */
	public EcoreVertex getNodeById(EcoreGraph graph, String id){
		for (EcoreVertex currentVertex : graph.getVertex()) {
			if(currentVertex.getVertexId().equals(id))
				return currentVertex;
		} return null;
	}
	
	/**
	 * Indicates if there is a path from the origin to the destination.
	 * @param origin
	 * @param destination
	 * @return
	 */
	public boolean thereIsPath(EcoreVertex origin, EcoreVertex destination){
		this.resetVisited();
		return origin.thereIsPath(destination);
	}
	
	/**
	 * Returns a vertex of the node such that the name of the corresponding dependenvies graph matches
	 * the string in the parameter. 
	 * @param name
	 * @return
	 */
	public EcoreGroup getGroupByDependenciesGraphName(String name) {
		for (EcoreGroup _group : groups) {
			if(_group.getDependenciesGraphVertex() != null 
					&& _group.getDependenciesGraphVertex().getIdentifier().equals(name))
				return _group;
		}
		return null;
	}
	
	/**
	 * Puts all the visited flag in false for all the vertex in the graph.
	 */
	public void resetVisited(){
		for (EcoreVertex vertex : this.vertex) {
			vertex.setVisited(false);
		}
	}
	
	// -----------------------------------------------
	// Static services
	// -----------------------------------------------

	/**
	 * Returns an identifier for the group in the parameter.
	 * @param languageModule. Group under study.
	 * @return
	 */
	public static String getLanguageModuleName(ArrayList<EcoreVertex> languageModule){
		return languageModule.get(0).getClassifier().getName() + "Module";
	}
	
	/**
	 * Returns a the collection of eclassifiers included in the group in the parameter. 
	 * @param group. Group under study.
	 * @return
	 */
	public static Collection<EClassifier> collectEClassifierByGroup(
			ArrayList<EcoreVertex> group) {
		Collection<EClassifier> collection = new ArrayList<EClassifier>();
		for (EcoreVertex ecoreVertex : group) {
			collection.add(ecoreVertex.getClassifier());
		}
		return collection;
	}

	public EcoreGraph cloneGraph() {
		EcoreGraph clone = new EcoreGraph();
		
		for (EcoreVertex vertex : this.vertex) {
			EcoreVertex clonedVertex = vertex.cloneVertex();
			clone.getVertex().add(clonedVertex);
		}
		
		for (EcoreArc arc : this.arcs) {
			EcoreVertex clonedFrom = clone.getNodeById(clone, arc.getFrom().getVertexId());
			EcoreVertex clonedTo = clone.getNodeById(clone, arc.getTo().getVertexId());
			EcoreArc clonedArc = arc.cloneArc(clonedFrom, clonedTo);
			clonedFrom.getOutgoingArcs().add(clonedArc);
			clonedTo.getIncomingArcs().add(clonedArc);
			clone.getArcs().add(clonedArc);
		}
		
		return clone;
	}

	public void reOrderVertex(EcoreGraph graph) {
		 ArrayList<EcoreVertex> newVertex = new ArrayList<EcoreVertex>();
		 for (EcoreVertex ecoreVertex : graph.getVertex()) {
			newVertex.add(this.getNodeById(this, ecoreVertex.getVertexId()));
		 }
		 this.vertex = newVertex;
	}

	public EcoreGroup getEcoreGroupByDependencyNode(Vertex dependencyVertex) {
		for (EcoreGroup group : this.groups) {
			if(group.getDependenciesGraphVertex().equals(dependencyVertex))
				return group;
		}
		return null;
	}
}