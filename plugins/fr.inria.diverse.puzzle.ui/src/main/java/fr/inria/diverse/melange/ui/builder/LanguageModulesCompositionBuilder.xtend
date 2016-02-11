package fr.inria.diverse.melange.ui.builder

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.emf.ecore.resource.Resource
import fr.inria.diverse.melange.metamodel.melange.ModelTypingSpace
import fr.inria.diverse.puzzle.adl.language.puzzle.LanguageBinding
import fr.inria.diverse.melange.metamodel.melange.ModelType
import fr.inria.diverse.k3.sle.common.utils.ModelUtils
import org.eclipse.emf.ecore.EPackage
import fr.inria.diverse.puzzle.adl.language.puzzle.Binding
import fr.inria.diverse.melange.metamodel.melange.Language
import org.eclipse.emf.ecore.util.EcoreUtil
import fr.inria.diverse.sle.puzzle.merge.impl.PuzzleMerge
import fr.inria.diverse.puzzle.match.vo.MatchingDiagnostic
import fr.inria.diverse.puzzle.match.impl.PuzzleMatch

/**
 * Builder for the action: Analyze Family.
 * Loads the input and performs the delegation to the corresponding plug-in.
 * 
 * @author David Mendez-Acuna
 */
class LanguageModulesCompositionBuilder extends AbstractBuilder {
	
	def String composeLanguageModules(Resource puzzleResource, Resource melangeResource, IProject project, IProgressMonitor monitor) {
		val bindingSpace = puzzleResource.contents.head as LanguageBinding
		val modelTypingSpace = melangeResource.contents.head as ModelTypingSpace
		EcoreUtil.resolveAll(modelTypingSpace);
		
		var String answer = 'Puzzle diagnostic: \n\n';
		
		// Obtaining required and provided interfaces for each binding
		for(var int i = 0; i < bindingSpace.binding.size; i++){
			var Binding binding = bindingSpace.binding.get(i)
			
			val requiredModelTypeName = binding.left
			val providedModelTypeName = binding.right
			
			val ModelType requiredModelType = modelTypingSpace.elements.findFirst[ element |
				element instanceof ModelType && (element as ModelType).name.equals(requiredModelTypeName)] as ModelType
			
			val ModelType providedModelType = modelTypingSpace.elements.findFirst[ element |
				element instanceof ModelType && (element as ModelType).name.equals(providedModelTypeName)] as ModelType
				
			val EPackage requiredModelTypeEPackage = ModelUtils.loadEcoreResource(requiredModelType.ecoreUri)
			val EPackage providedModelTypeEPackage = ModelUtils.loadEcoreResource(providedModelType.ecoreUri)
			
			val Language requiringLanguage = modelTypingSpace.elements.findFirst[ element |
				element instanceof Language && (element as Language).requires.exists[ req | req.name.equals(requiredModelTypeName)]] as Language
			
			val Language providingLanguage = modelTypingSpace.elements.findFirst[ element |
				element instanceof Language && requiringLanguage != element && (element as Language).implements.exists[ impl | 
					impl.name.equals(providedModelTypeName)
				]] as Language
			
			val EPackage requiredLanguageEPackage = ModelUtils.loadEcoreResource(requiringLanguage.syntax.ecoreUri)
			val EPackage providedLanguageEPackage = ModelUtils.loadEcoreResource(providingLanguage.syntax.ecoreUri)
			
			println('Data ... requiredModelTypeEPackage: ' + requiredModelTypeEPackage.name + ' - '
				+ 'providedModelTypeEPackage: ' + providedModelTypeEPackage.name + ' - '
				+ 'requiringLanguage: ' + requiringLanguage.name + ' - '
				+ 'providingLanguage: ' + providingLanguage.name + ' - '
			)
			
			val MatchingDiagnostic comparison = PuzzleMatch.instance.match(requiredLanguageEPackage, providedLanguageEPackage)
			
			var EPackage recalculatedRequiredInterface = PuzzleMerge.getInstance().
				recalculateRequiredInterface(providedLanguageEPackage, 
						comparison, "merged", requiredLanguageEPackage);
			 
			PuzzleMerge.instance.mergeAbstractSyntax(providedLanguageEPackage, providedModelTypeEPackage, 
				requiredLanguageEPackage, requiredModelTypeEPackage, comparison, recalculatedRequiredInterface, "")
			
		}
		return answer
	}
}