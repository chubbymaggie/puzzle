package flowchart

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import java.util.Hashtable
import FlowchartModule.Decision

@Aspect(className=Decision)
public class DecisionAspect {
	def void eval(Hashtable<String, Object> context) {
		_self.guard.evalConstraint(context)
	}
}