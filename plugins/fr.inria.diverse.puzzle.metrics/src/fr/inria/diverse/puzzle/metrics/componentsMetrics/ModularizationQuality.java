package fr.inria.diverse.puzzle.metrics.componentsMetrics;

import java.util.Hashtable;

import fr.inria.diverse.k3.sle.common.graphs.EcoreGraph;

public class ModularizationQuality {

	// -------------------------------------------------------------
	// Attributes
	// -------------------------------------------------------------
	
	private IntraConnectivty intraConnectivity;
	private InterConnectivity interConnectivity;
	
	// -------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------
	
	public ModularizationQuality(){
		this.intraConnectivity = new IntraConnectivty();
		this.interConnectivity = new InterConnectivity();
	}
	
	// -------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------
	
	public double compute(EcoreGraph modularizationGraph){
		Hashtable<String, Double>  intraConnectivityData = intraConnectivity.compute(modularizationGraph);
		Hashtable<String, Hashtable<String, Double>> interConnectivityData = interConnectivity.compute(modularizationGraph);
		
		double k = modularizationGraph.getGroups().size();
		
		double sumA = intraConnectivity.computeSum(intraConnectivityData);
		double left = (1/k) * sumA;
		
		double sumE = interConnectivity.computeSum(interConnectivityData);
		double right = (1 / (k*(k-1)/2)) * sumE;
		
		return left - right;
	}
}
