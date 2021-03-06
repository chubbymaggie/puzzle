package fr.inria.diverse.puzzle.configurator;

import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;

import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomain;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.Relation;
import fr.inria.diverse.puzzle.configurator.vos.Configuration;
import fr.inria.diverse.puzzle.configurator.vos.Option;
import fr.inria.diverse.puzzle.configurator.vos.Question;
import fr.inria.diverse.puzzle.configurator.vos.Option.State;

/**
 * Responsible for performing the multi-staged configuration process.
 * 
 * @author Jose A. Galindo
 *
 */
public class Configurator {

	// --------------------------------------------------
	// Attributes
	// --------------------------------------------------

	private FAMAFeatureModel fm;

	private Configuration conf;

	private Scanner reader;

	// --------------------------------------------------
	// Methods
	// --------------------------------------------------

	/**
	 * Initializes the configuration tool by setting up the corresponding
	 * feature model and creating the configuration questions.
	 */
	public void initialize() {
		conf = new Configuration();

		// this create the configuration
		Feature feat = fm.getRoot();
		processFeatureInorder(feat);

		// This order impacts the performance of the solver
		for (Question q : conf.questions) {
			if (q.hasUndecided()) {// if there are no undecided options we pass
				askUser(q);
				propagate(false);
			}
		}
		propagate(true);

		System.out.println(conf);
	}

	/**
	 * Performs the configuration process by interacting with the language
	 * designer in the console.
	 * 
	 * @param fm
	 */
	public void configure(FAMAFeatureModel fm) {
		this.fm = fm;
		reader = new Scanner(System.in);
		initialize();
		reader.close();
	}

	/**
	 * Ask the question in the parameter to the language designer by using the
	 * console.
	 * 
	 * @param q
	 */
	private void askUser(Question q) {
		// Implementing a VERY simple terminal configurator

		System.out.println("The available options are:");
		for (Option o : q.getOptions()) {
			// we only ask about undecided options
			if (o.getState() == State.undecided) {
				System.out.println(o.getName());
			}
		}
		System.out.println("Select the options. Divide by using ; :");

		String input = reader.nextLine();
		StringTokenizer tokenizer = new StringTokenizer(input, ";");
		while (tokenizer.hasMoreTokens()) {
			String qname = tokenizer.nextToken();
			conf.setOption(new Feature(qname), true);
		}
	}

	/**
	 * Sorts the questions that will be asked to the language designer.
	 * 
	 * @param feat
	 */
	public void processFeatureInorder(Feature feat) {
		// This order impacts the performance of the solver

		Iterator<Relation> relations = feat.getRelations();
		while (relations.hasNext()) {
			Relation next = relations.next();
			Question q = new Question(next.getName());
			Iterator<Feature> destination = next.getDestination();
			while (destination.hasNext()) {
				Feature next2 = destination.next();
				q.getOptions().add(new Option(next2.getName()));
				processFeatureInorder(next2);
			}
			conf.questions.add(q);

		}
	}

	public void propagate(boolean fullpropagation) {
		// mirar si usar choco que permite bichear los valores de cada variable
		ChocoReasoner reasoner = new ChocoReasoner();
		fm.transformTo(reasoner);
		es.us.isa.FAMA.stagedConfigManager.Configuration famaConfiguration = conf
				.getFAMAConfiguration();

		// First, we check that the initial config is valid
		reasoner.applyStagedConfiguration(famaConfiguration);
		Model chocoProblem = reasoner.getProblem();

		boolean valid = true;
		Solver solver = new CPSolver();
		solver.read(chocoProblem);
		if (!fullpropagation) {
			try {

				solver.propagate();
			} catch (ContradictionException e) {
				valid = false;
			}
		} else {
			valid = solver.solve();
		}
		if (!valid) {
			throw new IllegalStateException(
					"This should not happen. Is the input model valid?");
		} else {
			Iterator<IntegerVariable> intVarIterator = chocoProblem
					.getIntVarIterator();
			while (intVarIterator.hasNext()) {

				IntegerVariable next = intVarIterator.next();
				if (!next.getName().startsWith("_")) {
					IntDomainVar var = solver.getVar(next);
					IntDomain domain = var.getDomain();
					if (domain.getSize() == 1) {
						// has a fixed value
						int inf = domain.getInf();
						if (inf == 0) {
							System.out.println(next.getName()
									+ " is gonna be set to 0");
							conf.setOption(new Feature(next.getName()), false);
						} else if (inf == 1) {
							System.out.println(next.getName()
									+ " is gonna be set to 1");
							conf.setOption(new Feature(next.getName()), true);

						} else {
							// this is not a boolean feature
						}
					} else {
						// still undecided
						System.out.println(next.getName() + " is undecided");

					}
				}
			}
		}
	}
}
