package flowchartpck

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod

import java.util.Hashtable
import flowchartpck.Literal

@Aspect(className=Literal)
public class LiteralAspect extends ExpressionAspect{

	@OverrideAspectMethod
	def Object eval (Hashtable<String, Object> context) {
		if(_self instanceof IntegerLit)
			return (_self as IntegerLit).value
		else if(_self instanceof StringLit)
			return (_self as StringLit).value
		else if(_self instanceof BoolLit)
			return (_self as BoolLit).value
		else 
			return 0
	}
}