package fr.inria.diverse.puzzle.abc.semantics

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import abc.AbcPackage
import abc.A

import static extension fr.inria.diverse.puzzle.abc.semantics.AAspect.*

class ABCInterpreter {

	new() {
	}

	def eval(String modelPath) {
		var fact = new XMIResourceFactoryImpl
		if (!EPackage.Registry.INSTANCE.containsKey(AbcPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(AbcPackage.eNS_URI, AbcPackage.eINSTANCE);
		}
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

		var rs = new ResourceSetImpl()

		var uri = URI.createFileURI(modelPath);
		var res = rs.getResource(uri, true);
		var A a = res.contents.get(0) as A
		a.eval()
	}
	
	def static void main(String[] args){
		(new ABCInterpreter()).eval('instances/abc.xmi')
	}
}
