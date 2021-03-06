package flowchartpck

import fr.inria.diverse.k3.al.annotationprocessor.Aspect

import flowchartpck.Flowchart
import flowchartpck.Start

import static extension flowchartpck.StartAspect.*

import java.util.Hashtable

@Aspect(className=Flowchart)
public class FlowchartAspect {

	def void exec() {
		var Hashtable<String, Object> context = new Hashtable<String, Object>()
		_self.nodes.findFirst[ node | node instanceof Start ].eval(context)
	}
}