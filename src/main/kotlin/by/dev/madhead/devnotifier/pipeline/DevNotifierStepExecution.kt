package by.dev.madhead.devnotifier.pipeline

import by.dev.madhead.devnotifier.DevNotifier
import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.creator.EmailCreator
import by.dev.madhead.devnotifier.util.createDefaultAddressesSet
import hudson.AbortException
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution

class DevNotifierStepExecution(
        context: StepContext,
        val defaultRecipients: String?) : SynchronousNonBlockingStepExecution<Void>(context) {

    override fun run(): Void? {
        val run = context.get(WorkflowRun::class.java)
                ?: throw AbortException(Messages.devnotifier_hudson_AbortException_workflowRunRequired())
        val listener = context.get(TaskListener::class.java)
                ?: throw AbortException(Messages.devnotifier_hudson_AbortException_taskListenerRequired())
        try {
            DevNotifier().sendMail(
                    email = EmailCreator(run, listener,
                            context.get(FilePath::class.java)
                                    ?: throw AbortException(Messages.devnotifier_hudson_AbortException_workspaceRequired())
                    ).create(),
                    defaultRecipientsAddresses = createDefaultAddressesSet(run, defaultRecipients),
                    logger = listener.logger)
            return null
        } catch (exception: Throwable) {
            throw AbortException(exception.message)
        }
    }

}
