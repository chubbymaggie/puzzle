package fr.inria.diverse.melange.ui.vos

class CompositionArc {
	
	// -------------------------------------------------
	// Attributes
	// -------------------------------------------------
	
	private CompositionNode from
	private CompositionNode to
	
	// -------------------------------------------------
	// Methods
	// -------------------------------------------------
	
	def CompositionNode getFrom(){
		return from
	}
	
	def void setFrom(CompositionNode from){
		this.from = from
	}
	
	def CompositionNode getTo(){
		return to
	}
	
	def void setTo(CompositionNode to){
		this.to = to
	}
}