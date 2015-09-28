package fr.inria.diverse.puzzle.instancesgenerator.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import fr.inria.diverse.k3.sle.common.graphs.DependencyArc;
import fr.inria.diverse.k3.sle.common.graphs.DependencyGraph;
import fr.inria.diverse.k3.sle.common.utils.PCMQueryServices;
import fr.inria.diverse.puzzle.implicationSolver.SimpleSolver;

public class PCMFillerGenerator {
	
	public static String generatePCMs(DependencyGraph graph, String PCM){
	
		SimpleSolver solver = new SimpleSolver();
		System.out.println("The input PCM can be depicted by using the following requires/excludes:");
		for(DependencyArc arc:graph.getArcs()){
			System.out.println("Requires: "+arc.getFrom().getIdentifier()+";"+ arc.getTo().getIdentifier());

			solver.createRequires("\"" + arc.getFrom().getIdentifier() + "\"", "\"" + arc.getTo().getIdentifier() + "\"");
		}
		
		for(String f: getAllCoreMandatory(PCM)){
			System.out.println("Mandatory:"+f);
			solver.createMandatory(f);
		}
		
		for(String rel: getAllRequires(PCM) ){
			System.out.println("Requires: "+rel);
			String orig= rel.substring(0, rel.indexOf(';'));
			String dest= rel.substring(rel.indexOf(';') + 1, rel.length());
			solver.createRequires(orig, dest);
		}
		
		for(String rel: getAllExcludes(PCM) ){
			System.out.println("Excludes: "+rel);

			String orig= rel.substring(0, rel.indexOf(';'));
			String dest= rel.substring(rel.indexOf(';') + 1, rel.length());
			solver.createExcludes(orig, dest);
		}
		
		return solver.solve();
	}

	public static Collection<String> getAllCoreMandatory(String PCM) {
		Collection<String> res = new LinkedList<String>();
		PCMQueryServices.getInstance().loadPCM(PCM);
		Set<String> getAllFeatures = PCMQueryServices.getInstance().getAllFeatures();
		for (String feature : getAllFeatures) {
			if (!(PCMQueryServices.getInstance().existsProductWithoutFeature(feature))) {
				res.add(feature);
			}
		}
		return res;
	}

	public static Collection<String> getAllRequires(String PCM) {
		Collection<String> res = new LinkedList<String>();
		PCMQueryServices.getInstance().loadPCM(PCM);
		Set<String> allFeatures = PCMQueryServices.getInstance().getAllFeatures();
		for (String orig : allFeatures) {
			for (String dest : allFeatures) {
			//	System.out.println(orig+";"+dest);
				if (!orig.equals(dest) && PCMQueryServices.getInstance().allProductsWithFeatureAHaveAlsoFeatureB(orig, dest)) {
					res.add(orig + ";" + dest);
				}
			}
		}
		return res;
	}

	public static Collection<String> getAllExcludes(String PCM) {
		Collection<String> res = new LinkedList<String>();

		PCMQueryServices.getInstance().loadPCM(PCM);
		for (String origin : PCMQueryServices.getInstance().getAllFeatures()) {
			for (String destination : PCMQueryServices.getInstance().getAllFeatures()) {
				if (!origin.equals(destination)) {
					if (!origin.equals(destination) && PCMQueryServices.getInstance().allProductsWithFeatureAExcludeFeatureB(origin, destination)){
						res.add(origin+";"+destination);
					}
				}
			}
		}
		return res;
	}
}
