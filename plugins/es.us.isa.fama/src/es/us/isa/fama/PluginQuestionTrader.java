package es.us.isa.fama;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Iterator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.ConfigurableQuestionFactory;
import es.us.isa.FAMA.Reasoner.Question;
import es.us.isa.FAMA.Reasoner.QuestionTrader;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.stagedConfigManager.Configuration;

public class PluginQuestionTrader extends QuestionTrader {

	@SuppressWarnings("unchecked")
	public PluginQuestionTrader() {
		super("OSGI");
		try {
			this.reasonersIdMap.put("Choco", this.createReasoner("Choco",
					es.us.isa.ChocoReasoner.ChocoReasoner.class));
			this.reasonersIdMap.put("ChocoAttributed", this.createReasoner("ChocoAttributed", 
					 es.us.isa.ChocoReasoner.attributed.ChocoReasoner.class));
			
			// this.reasonersIdMap.put("Choco4exp",
			// this.createReasoner("Choco4exp",
			// es.us.isa.ChocoReasoner4Exp.ChocoReasoner.class));
			// this.reasonersIdMap.put("ChocoAttributed4exp",
			// this.createReasoner("ChocoAttributed4exp",
			// es.us.isa.ChocoReasoner4Exp.attributed.ChocoReasoner.class));
			// this.reasonersIdMap.put("Sat4j", this.createReasoner("Sat4j",
			// es.us.isa.Sat4jReasoner.Sat4jReasoner.class));

			this.questionsMap.put("Products", (Class<Question>) Class.forName("es.us.isa.FAMA.Reasoner.questions.ProductsQuestion"));
			this.questionsMap.put("#Products", (Class<Question>) Class.forName("es.us.isa.FAMA.Reasoner.questions.NumberOfProductsQuestion"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	private Reasoner createReasoner(String reasonerName, Class reasonerClass)
			throws Exception {
		URL configFilePath = Platform.getBundle("es.us.isa.fama").getEntry(
				"/" + reasonerName + "Config.xml");
		File configFile = new File(FileLocator.resolve(configFilePath).toURI());

		Reasoner reasoner = (Reasoner) reasonerClass.newInstance();
		ConfigurableQuestionFactory qFact = new ConfigurableQuestionFactory();
		qFact.setClassLoader(reasoner.getClass().getClassLoader());
		qFact.parseConfigFile(new FileInputStream(configFile));
		reasoner.setFactory(qFact);
		this.reasoners.add(reasoner);
		return reasoner;
	}

	public PerformanceResult ask(Question q) {
		PerformanceResult res = null;
		if (q != null) {
			Class<? extends Reasoner> reasonerClass = q.getReasonerClass();
			Iterator<Reasoner> itr = reasoners.iterator();
			while (itr.hasNext() && res == null) {
				Reasoner r = itr.next();
				if (reasonerClass.isInstance(r)) {
					fm.transformTo(r);
					Iterator<Configuration> configIterator = configurations
							.iterator();
					while (configIterator.hasNext()) {
						r.applyStagedConfiguration(configIterator.next());
					}
					res = r.ask(q);
					r.unapplyStagedConfigurations();
					selector.registerResults(q, fm, res);
				}
			}
		}
		return res;
	}
}