package fr.inria.diverse.k3.sle.common.tuples;

import java.util.ArrayList;
import java.util.List;

public class SemanticNode {

	private String id;
	private String label;
	private SemanticNode parent;
	private List<SemanticNode> childs;
	private String members;

	public SemanticNode(String id, String label, SemanticNode parent, String members){
		this.id = id;
		this.label = label;
		this.parent = parent;
		childs = new ArrayList<SemanticNode>();
		this.members = members;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public SemanticNode getParent() {
		return parent;
	}

	public void setParent(SemanticNode parent) {
		this.parent = parent;
	}

	public List<SemanticNode> getChilds() {
		return childs;
	}

	public String getMembers() {
		return members;
	}

	public void setMembers(String members) {
		this.members = members;
	}
}