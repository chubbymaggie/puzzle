package solver;

import static choco.Choco.eq;
import static choco.Choco.gt;
import static choco.Choco.implies;
import static choco.Choco.makeIntVar;

import java.util.HashMap;
import java.util.Map;

import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;

public class VerySimpleSolver {

	String[] features, requiresGraph, requiresPCM, excludesGraph, excludesPCM,
			coreMandatory;

	Map<String, IntegerVariable> variables;
	Model p;

	public VerySimpleSolver(String[] features, String[] requiresGraph,
			String[] requiresPCM, String[] excludesGraph, String[] excludesPCM,
			String[] coreMandatory) {
		this.features = features;
		this.requiresGraph = requiresGraph;
		this.requiresPCM = requiresPCM;
		this.excludesGraph = excludesGraph;
		this.excludesPCM = excludesPCM;
		this.coreMandatory = coreMandatory;
		variables = new HashMap<String, IntegerVariable>();
		p = new CPModel();
		this.transform();
	}

	private void transform() {
		// add variables
		for (String feature : features) {
			p.addVariable(createVariable(feature));
		}

		// add mandatory
		for (String feature : coreMandatory) {
			p.addConstraint(createMandatory(feature));
		}

		// add requiresGraph
		for (String requires : requiresGraph) {
			String orig = requires.substring(0, requires.indexOf(';'));
			String dest = requires.substring(requires.indexOf(';') + 1,
					requires.length());
			p.addConstraint(createRequires(orig, dest));
		}

		// add requiredPCM
		for (String requires : requiresPCM) {
			String orig = requires.substring(0, requires.indexOf(';'));
			String dest = requires.substring(requires.indexOf(';') + 1,
					requires.length());
			p.addConstraint(createRequires(orig, dest));
		}

		// add excludessGraph
		for (String excludes : excludesGraph) {
			String orig = excludes.substring(0, excludes.indexOf(';'));
			String dest = excludes.substring(excludes.indexOf(';') + 1,
					excludes.length());
			p.addConstraint(createExcludes(orig, dest));
		}

		// add excludesPCM
		for (String excludes : excludesPCM) {
			String orig = excludes.substring(0, excludes.indexOf(';'));
			String dest = excludes.substring(excludes.indexOf(';') + 1,
					excludes.length());
			p.addConstraint(createExcludes(orig, dest));
		}

	}

	private Constraint createMandatory(String feature) {
		return eq(variables.get(feature), 1);
	}

	public IntegerVariable createVariable(String feature) {
		int[] domain = { 0, 1 };
		IntegerVariable var = makeIntVar(feature, domain);
		variables.put(feature, var);
		return var;
	}

	public Constraint createRequires(String from, String to) {

		IntegerVariable originVar = variables.get(from);
		IntegerVariable destinationVar = variables.get(to);
		Constraint requiresConstraint = implies(gt(originVar, 0),
				gt(destinationVar, 0));
		return requiresConstraint;

	}

	public Constraint createExcludes(String from, String to) {

		IntegerVariable originVar = variables.get(from);
		IntegerVariable destinationVar = variables.get(to);
		Constraint requiresConstraint = implies(gt(originVar, 0),
				eq(destinationVar, 0));
		return requiresConstraint;
	}

	public void solve() {

		Solver solver = new CPSolver();
		solver.read(p);

		int n = 0;
		if (solver.solve() == Boolean.TRUE && solver.isFeasible()) {
			do {
				System.out.println("Product " + (n++));
				for (int i = 0; i < p.getNbIntVars(); i++) {
					IntDomainVar aux = solver.getVar(p.getIntVar(i));
					// System.out.print(aux.getName()+":"+aux.getVal());
					if (aux.getVal() > 0) {
						System.out.print(aux.getName() + ";");
					}
				}
				System.out.println("");
			} while (solver.nextSolution() == Boolean.TRUE);
		}

	}
}
