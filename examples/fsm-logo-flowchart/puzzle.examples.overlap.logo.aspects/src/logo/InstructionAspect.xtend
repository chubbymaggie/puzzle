package logo

import fr.inria.diverse.k3.al.annotationprocessor.Aspect

import kmLogo.Instruction
import kmLogo.Primitive
import kmLogo.VarDeclaration

import java.util.Hashtable

import static extension logo.PrimitiveAspect.*
import static extension logo.VarDeclarationAspect.*

// *.*
// ASPECT
 @Aspect(className=Instruction) 
class InstructionAspect {
 
	def public void eval (Hashtable<String, Object> context ) {
		if(_self instanceof Primitive)
			(_self as Primitive).eval(context)
		else if(_self instanceof VarDeclaration)
			(_self as VarDeclaration).eval(context)
	}  
}