package fr.inria.diverse.melange.ui.builder

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.emf.ecore.resource.Resource
import fr.inria.diverse.melange.metamodel.melange.ModelTypingSpace
import fr.inria.diverse.melange.metamodel.melange.ModelType
import fr.inria.diverse.k3.sle.common.utils.ModelUtils
import org.eclipse.emf.ecore.EPackage
import fr.inria.diverse.puzzle.validator.command.ValidatorImpl
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl
import fr.inria.diverse.puzzle.validator.vos.PuzzleDiagnosis
import fr.inria.diverse.puzzle.match.vo.MatchingDiagnostic
import fr.inria.diverse.puzzle.match.impl.PuzzleMatch
import fr.inria.diverse.puzzle.language.binding.Binding
import fr.inria.diverse.puzzle.language.binding.LanguageBinding

/**
 * Builder for the action: Analyze Family.
 * Loads the input and performs the delegation to the corresponding plug-in.
 * 
 * @author David Mendez-Acuna
 */
class ValidateLanguageModulesComposabilityBuilder extends AbstractBuilder {
	
	def String validateLanguageModulesComposability(Resource puzzleResource, Resource melangeResource, IProject project, IProgressMonitor monitor) {
		val bindingSpace = puzzleResource.contents.head as LanguageBinding
		val modelTypingSpace = melangeResource.contents.head as ModelTypingSpace
		
		var String answer = 'Puzzle diagnostic: \n\n';
		
		// Obtaining required and provided interfaces for each binding
		for(var int i = 0; i < bindingSpace.binding.size; i++){
			var Binding binding = bindingSpace.binding.get(i)
			
			val requiredModelTypeName = binding.right
			val providedModelTypeName = binding.left
			
			val ModelType requiredModelType = modelTypingSpace.elements.findFirst[ element |
				element instanceof ModelType && (element as ModelType).name.equals(requiredModelTypeName)] as ModelType
			
			val ModelType providedModelType = modelTypingSpace.elements.findFirst[ element |
				element instanceof ModelType && (element as ModelType).name.equals(providedModelTypeName)] as ModelType
				
			val EPackage requiredEPackage = ModelUtils.loadEcoreResource(requiredModelType.ecoreUri)
			val EPackage providedEPackage = ModelUtils.loadEcoreResource(providedModelType.ecoreUri)
			
			Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());

			val MatchingDiagnostic comparison = PuzzleMatch.instance.match(requiredEPackage, providedEPackage)
			val PuzzleDiagnosis diagnosis = ValidatorImpl.instance.checkCompatibility(requiredEPackage, providedEPackage, comparison)
		
			if(diagnosis == null){
				answer += '    ' + requiredModelTypeName + ' - ' + providedModelTypeName + ': COMPATIBLE \n'
			}
			else{
				answer += '    ' + requiredModelTypeName + ' - ' + providedModelTypeName + ': NOT COMPATIBLE \n'
			}
		}
		return answer
	}
}