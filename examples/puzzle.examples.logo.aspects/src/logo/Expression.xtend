package logo

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import kmLogo.Expression

@Aspect(className=Expression)
public class ExpressionAspect {

	def Object eval (Context context) {
		return 0 
	}

}