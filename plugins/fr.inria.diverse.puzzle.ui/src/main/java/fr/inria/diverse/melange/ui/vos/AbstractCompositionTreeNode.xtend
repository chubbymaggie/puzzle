package fr.inria.diverse.melange.ui.vos

import fr.inria.diverse.melange.metamodel.melange.Language

class AbstractCompositionTreeNode {

	// -------------------------------------------------
	// Attributes
	// -------------------------------------------------
	
	private CompositionGraph graph
	
	// -------------------------------------------------
	// Constructor
	// -------------------------------------------------
	
	new(CompositionGraph graph){
		this.graph = graph
	}
	
	// -------------------------------------------------
	// Methods
	// -------------------------------------------------
	
	def boolean existsDependency(CompositionTreeLeaf node){
		if(this instanceof CompositionTreeLeaf){
			var CompositionTreeLeaf thisLeaf = this as CompositionTreeLeaf
			if(graph.depends(thisLeaf.language, node.language))
				return true
			else
				return false
		}else if(this instanceof CompositionTreeNode){
			var CompositionTreeNode thisNode = this as CompositionTreeNode
			return thisNode._requiring.existsDependency(node) || thisNode._providing.existsDependency(node)
		}
		return false
	}
	
	/** 
	 * Adds a node to the current tree
	 */
	def AbstractCompositionTreeNode addNode(Language language){
		if(this instanceof CompositionTreeLeaf){
			var CompositionTreeLeaf currentLeaf = this as CompositionTreeLeaf
			var CompositionTreeLeaf newLeaf = new CompositionTreeLeaf(graph, language)
			var CompositionTreeNode node = new CompositionTreeNode(graph)
			
			if(graph.depends(currentLeaf.language, language)){
				node._requiring = currentLeaf
				node._providing = newLeaf
			}else{
				node._requiring = newLeaf
				node._providing = currentLeaf
			}
			return node
		}else{
			var CompositionTreeNode currentNode = this as CompositionTreeNode
			var CompositionTreeLeaf leaf = new CompositionTreeLeaf(this.graph, language)
			
			if(currentNode._providing.existsDependency(leaf)){
				var CompositionTreeNode newNode = new CompositionTreeNode(graph)
				newNode._providing = currentNode._providing.addNode(language)
				newNode._requiring = currentNode._requiring
				return newNode
			}else{
				var CompositionTreeNode newNode = new CompositionTreeNode(graph)
				newNode._providing = currentNode._providing
				newNode._requiring = currentNode._requiring.addNode(language)
				return newNode
			}
		}
	}
}