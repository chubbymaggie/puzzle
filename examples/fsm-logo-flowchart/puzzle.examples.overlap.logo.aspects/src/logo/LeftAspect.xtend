package logo

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod

import kmLogo.Left
import java.util.Hashtable

import static extension logo.ExpressionAspect.*

@Aspect(className=Left)
public class LeftAspect extends PrimitiveAspect{
 
	@OverrideAspectMethod
	def void eval (Hashtable<String, Object> context) {
		var int param = (-1)  *_self.angle.eval(context) as Integer
		println("LEFT " + param)
		(context.get('turtle') as Turtle).rotate(param)
	}
}