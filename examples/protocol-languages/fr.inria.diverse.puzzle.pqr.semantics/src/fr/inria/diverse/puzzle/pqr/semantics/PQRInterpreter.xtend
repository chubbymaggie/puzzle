package fr.inria.diverse.puzzle.pqr.semantics

import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl

import static extension fr.inria.diverse.puzzle.pqr.semantics.PAspect.*
import pqr.PqrPackage
import pqr.P

class PQRInterpreter {

	new() {
	}

	def eval(String modelPath) {
		var fact = new XMIResourceFactoryImpl
		if (!EPackage.Registry.INSTANCE.containsKey(PqrPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(PqrPackage.eNS_URI, PqrPackage.eINSTANCE);
		}
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fact);

		var rs = new ResourceSetImpl()

		var uri = URI.createFileURI(modelPath);
		var res = rs.getResource(uri, true);
		var P a = res.contents.get(0) as P
		a.eval()
	}
	
	def static void main(String[] args){
		(new PQRInterpreter()).eval('instances/pqr.xmi')
	}
}
