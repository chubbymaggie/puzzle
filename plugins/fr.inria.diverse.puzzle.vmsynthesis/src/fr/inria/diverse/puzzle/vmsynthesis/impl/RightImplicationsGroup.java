package fr.inria.diverse.puzzle.vmsynthesis.impl;

import java.util.ArrayList;

import vm.PFeatureRef;

public class RightImplicationsGroup {

	// -----------------------------------------------
	// Attributes
	// -----------------------------------------------
	
	private PFeatureRef rightSide;
	
	private ArrayList<PFeatureRef> leftSide;
	
	// -----------------------------------------------
	// Constructors
	// -----------------------------------------------
	
	public RightImplicationsGroup(PFeatureRef rightSide){
		this.rightSide = rightSide;
		this.leftSide = new ArrayList<PFeatureRef>();
	}

	// -----------------------------------------------
	// Getters and setters
	// -----------------------------------------------
	
	public PFeatureRef getRightSide() {
		return rightSide;
	}

	public void setRightSide(PFeatureRef rightSide) {
		this.rightSide = rightSide;
	}

	public ArrayList<PFeatureRef> getLeftSide() {
		return leftSide;
	}
	
	// -----------------------------------------------
	// Methods
	// -----------------------------------------------
	
	public String toString(){
		String leftSide = "";
		boolean first = true;
		for (PFeatureRef pFeatureRef : this.leftSide) {
			if(!first)
				leftSide += " and";
			
			leftSide += " " + pFeatureRef.getRef().getName();
			first = false;
		}
		return "(" + leftSide + " ) implies " + rightSide.getRef().getName();
	}
	
	public boolean equals(Object o){
		RightImplicationsGroup group = (RightImplicationsGroup) o;
		boolean rightSideEqual = this.rightSide.getRef().getName().
				equals(group.rightSide.getRef().getName());
		boolean leftSideEqual = true;
		for (PFeatureRef pFeatureRef : leftSide) {
			if(!this.containsFeature(group.getLeftSide(), pFeatureRef)){
				leftSideEqual = false;
				break;
			}
		}
		return rightSideEqual && leftSideEqual && group.getLeftSide().size() == this.leftSide.size();
	}

	private boolean containsFeature(ArrayList<PFeatureRef> leftSide2,
			PFeatureRef pFeatureRef) {
		for (PFeatureRef feature : leftSide2) {
			if(feature.getRef().getName().equals(pFeatureRef.getRef().getName()))
				return true;
		}
		return false;
	}
}
