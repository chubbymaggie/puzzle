package flowchartpck

import fr.inria.diverse.k3.al.annotationprocessor.Aspect

import java.util.Hashtable
import flowchartpck.Decision

import static extension flowchartpck.ConstraintAspect.*

@Aspect(className=Decision)
public class DecisionAspect extends NodeAspect {

	def void eval(Hashtable<String, Object> context) {
		var guard = _self.guard.evalConstraint(context)
		println('Evaluating constraint ' + guard)
		if(guard){
			_self.outgoing.get(0).target.eval(context)
		}else{
			_self.outgoing.get(1).target.eval(context)
		}
	}
}