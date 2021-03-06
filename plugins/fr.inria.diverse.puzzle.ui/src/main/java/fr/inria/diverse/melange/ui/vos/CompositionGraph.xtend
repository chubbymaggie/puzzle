package fr.inria.diverse.melange.ui.vos

import java.util.List
import java.util.ArrayList
import fr.inria.diverse.melange.metamodel.melange.Language
import fr.inria.diverse.melange.metamodel.melange.ModelTypingSpace
import fr.inria.diverse.puzzle.language.binding.Binding

class CompositionGraph {
	
	// -------------------------------------------------
	// Attributes
	// -------------------------------------------------
	
	private List<CompositionNode> nodes
	private List<CompositionArc> arcs
	
	// -------------------------------------------------
	// Constructor
	// -------------------------------------------------
	
	new(ArrayList<Language> bindedLanguages, List<Binding> statements, ModelTypingSpace modelTypingSpace){
		
		this.nodes = new ArrayList<CompositionNode>()
		this.arcs = new ArrayList<CompositionArc>()
		
		for(Language language : bindedLanguages){
			var CompositionNode newNode = new CompositionNode()
			newNode.language = language
			
			if(!this.nodes.contains(newNode))
				this.nodes.add(newNode)
		}
		
		for(Binding binding : statements){
			var CompositionArc newArc = new CompositionArc()
			
			val Language leftLanguage = modelTypingSpace.elements.findFirst[ element |
				element instanceof Language && (element as Language).requires.exists[ req | req.name.equals(binding.left)
					&& req.name.contains((element as Language).name)
				]] as Language
			
			val Language rightLanguage = modelTypingSpace.elements.findFirst[ element |
				element instanceof Language && leftLanguage != element && (element as Language).implements.exists[ impl | 
					impl.name.equals(binding.right) && impl.name.contains((element as Language).name)
				]] as Language
			
			newArc.from = findNode(leftLanguage)
			newArc.to = findNode(rightLanguage)
			this.arcs.add(newArc)
			
			newArc.from.outgoing.add(newArc)
			newArc.to.incoming.add(newArc)
		}
	}
	
	// -------------------------------------------------
	// Methods
	// -------------------------------------------------
	
	def boolean depends(Language from, Language to){
		var CompositionNode fromNode = findNode(from)
		var CompositionNode toNode = findNode(to)
		return fromNode.thereIsPath(toNode)
	}
	
	def CompositionNode findNode(Language language){
		return this.nodes.findFirst[node | node.language == language]
	}
	
	def List<CompositionNode> getNodes(){
		return nodes
	}
	
	def List<CompositionArc> getArcs(){
		return arcs
	}
}