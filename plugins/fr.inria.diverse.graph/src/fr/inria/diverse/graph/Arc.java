package fr.inria.diverse.graph;

/**
 * Value object representing an arc within a graph.
 * 
 * @author David Mendez-Acuna
 *
 */
public class Arc {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	protected Vertex from;
	protected Vertex to;
	
	// -----------------------------------------------
	// Constructor
	// -----------------------------------------------
	
	public Arc(Vertex from, Vertex to) {
		super();
		
		this.from = from;
		this.from.getOutgoingArcs().add(this);
		
		this.to = to;
		this.to.getIncomingArcs().add(this);
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	public String toString(){
		return this.from.getIdentifier() + " -> " + this.to.getIdentifier();
	}
	
	public boolean equals(Object o){
		Arc arc = (Arc) o;
		return this.getFrom().getIdentifier().equals(arc.getFrom().getIdentifier()) &&
				this.getTo().getIdentifier().equals(arc.getTo().getIdentifier());
	}
	
	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------

	public Vertex getFrom() {
		return from;
	}

	public void setFrom(Vertex from) {
		this.from = from;
	}

	public Vertex getTo() {
		return to;
	}

	public void setTo(Vertex to) {
		this.to = to;
	}
}