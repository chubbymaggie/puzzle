package FSMFlowcharts.constraint

import commons.*

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod
import java.util.Hashtable
import ConstraintModule.Conditional

import static extension constraint.ProgramAspect.*
@Aspect(className=Conditional)
class ConditionalAspect extends StatementAspect {
	
	@OverrideAspectMethod
	def void eval(Hashtable<String, Object> context){
		if(_self.condition.eval(context) as Boolean){
			_self.body.eval(context)
		}
	}
}