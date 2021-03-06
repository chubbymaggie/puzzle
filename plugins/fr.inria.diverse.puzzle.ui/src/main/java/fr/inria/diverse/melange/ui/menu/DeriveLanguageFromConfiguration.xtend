package fr.inria.diverse.melange.ui.menu

import javax.inject.Inject

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.core.commands.ExecutionException

import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.OperationCanceledException
import org.eclipse.core.runtime.Status
import fr.inria.diverse.melange.ui.builder.DeriveLanguageFromConfigurationBuilder

/**
 * Hanlder for the action: Configure
 * @author David Mendez-Acuna
 */
class DeriveLanguageFromConfiguration extends AbstractHandler {
	
	@Inject DeriveLanguageFromConfigurationBuilder builder
	
	override execute(ExecutionEvent event) throws ExecutionException {
		new Job("Puzzle: Deriving language from configuration") {
			override run(IProgressMonitor monitor) {
				try {
					monitor.beginTask("Puzzle: Deriving language from configuration", 4)

					val sel = HandlerUtil.getActiveMenuSelection(event)
					val selection = sel as IStructuredSelection
					val resources = selection.toArray
					val project = (selection.firstElement as IResource).project

					builder.deriveLanguageFromConfigurationBuilder(resources, project, monitor)
					
				} catch (OperationCanceledException e) {
					return Status.CANCEL_STATUS
				}catch (Exception e) {
					e.printStackTrace
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