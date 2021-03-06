package flowchartpck

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod

import java.util.Hashtable
import flowchartpck.Action

import static extension flowchartpck.ProgramAspect.*

@Aspect(className=Action)
public class ActionAspect extends NodeAspect {

	@OverrideAspectMethod
	def void eval(Hashtable<String, Object> context) {
		_self.doProgram.eval(context)
		println('   ... context = ' + context.toString)
		_self.outgoing.forEach[ arc | arc.target.eval(context)]
		
	}
}
