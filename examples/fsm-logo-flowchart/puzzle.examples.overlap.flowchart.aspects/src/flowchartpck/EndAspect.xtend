package flowchartpck

import fr.inria.diverse.k3.al.annotationprocessor.Aspect
import fr.inria.diverse.k3.al.annotationprocessor.OverrideAspectMethod

import java.util.Hashtable
import flowchartpck.End

@Aspect(className=End)
public class EndAspect extends NodeAspect {

	@OverrideAspectMethod
	def void eval(Hashtable<String, Object> context){
		println('Finalizing the execution of the flowchart')
		System.exit(0)
	}
}