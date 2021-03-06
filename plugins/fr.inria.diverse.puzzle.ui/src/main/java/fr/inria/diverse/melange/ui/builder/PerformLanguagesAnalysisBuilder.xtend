package fr.inria.diverse.melange.ui.builder

import org.eclipse.core.resources.IProject
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.emf.ecore.resource.Resource
import fr.inria.diverse.melange.metamodel.melange.ModelTypingSpace
import fr.inria.diverse.puzzle.metrics.actions.ComputeMetricsActionImpl
import fr.inria.diverse.k3.sle.common.vos.SynthesisProperties
import fr.inria.diverse.k3.sle.common.utils.ProjectManagementServices

/**
 * Builder for the action: Analyze Family.
 * Loads the input and performs the delegation to the corresponding plug-in.
 * 
 * @author David Mendez-Acuna
 */
class PerformLanguagesAnalysisBuilder extends AbstractBuilder {
	
	def void computeMetrics(Resource res, IProject project, IProgressMonitor monitor) {
		val root = res.contents.head as ModelTypingSpace
		var SynthesisProperties properties = this.synthesisProperties
		this.decorateProjectWithFolderStructure(project)
		ComputeMetricsActionImpl.instance.computeMetrics(properties, root, project);
	}
	
	/**
	 * Decorates the project in the parameter with the structure to store the reports
	 * @param project. Project to decorate.
	 */
	def decorateProjectWithFolderStructure(IProject project) {
		ProjectManagementServices.createFolderByName(project, "reports")
	}
}