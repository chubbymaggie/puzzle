package Expressions

import fmpl.FmplPackage
import fmpl.Policy
import java.io.BufferedReader
import java.io.FileReader
import java.io.PrintWriter
import java.util.Hashtable
import java.util.StringTokenizer
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

import static extension Expressions.AutomataAspect.*
import static extension Expressions.PolicyAspect.*

class ExpressionsInterpreter {

	String basepath

	new(String basepath) {
		this.basepath = basepath;
	}

	def eval(String modelPath) {
		var fact = new XMIResourceFactoryImpl
		if (!EPackage.Registry.INSTANCE.containsKey(FmplPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(FmplPackage.eNS_URI, FmplPackage.eINSTANCE);
		}
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

		var rs = new ResourceSetImpl()

		var uri = URI.createFileURI(modelPath);
		var res = rs.getResource(uri, true);
		var Policy policy = res.contents.get(0) as Policy

		var Hashtable<String, Object> context = new Hashtable<String, Object>();

		var BufferedReader br = new BufferedReader(new FileReader(basepath + "/input.txt"));
		var PrintWriter out = new PrintWriter(basepath + "/output.txt");

		for (var String line; (line = br.readLine()) != null;) {
			var StringTokenizer tokenizer = new StringTokenizer(line, ";");
			while (tokenizer.hasMoreElements()) {
				var String input = tokenizer.nextToken();
				policy.eval(context, input);
				out.println('ss')
				out.println("The current automata state after " + input + " is: " +
						policy.getAutomatas().get(0).currentState.name)

			}
		}
		out.flush;
		out.close;
	}
	
	def static void main(String[] args){
		(new ExpressionsInterpreter('input')).eval('models/input.xmi')
	}

}
