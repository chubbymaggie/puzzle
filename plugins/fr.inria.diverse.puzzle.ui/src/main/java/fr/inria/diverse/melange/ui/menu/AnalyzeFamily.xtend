package fr.inria.diverse.melange.ui.menu

import com.google.inject.Inject
import fr.inria.diverse.melange.ui.builder.MetricsBuilder
import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.jobs.Job
import org.eclipse.emf.common.util.URI
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.xtext.resource.DerivedStateAwareResource
import org.eclipse.xtext.ui.resource.XtextResourceSetProvider

class AnalyzeFamily extends AbstractHandler {
	
	@Inject MetricsBuilder builder
	@Inject XtextResourceSetProvider rsProvider

	override execute(ExecutionEvent event) throws ExecutionException {
		new Job("Puzzle: Analyzing family") {
			override run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Computing all metrics of the family's analysis", 4)

					val sel = HandlerUtil.getActiveMenuSelection(event)
					val selection = sel as IStructuredSelection
					val resource = selection.firstElement as IResource
					val project = resource.project
					val rs = rsProvider.get(project)
					val res = rs.getResource(URI::createPlatformResourceURI(resource.fullPath.toString, true), true) as DerivedStateAwareResource

					builder.computeMetrics(res, project, monitor)
					
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS
				} finally {
					monitor.done
				}

				return Status.OK_STATUS
			}
		}.schedule

		return null
	}
}