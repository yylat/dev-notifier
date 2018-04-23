package by.dev.madhead.jarvis.pipeline

import by.dev.madhead.jarvis.Jarvis
import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.creator.EmailCreator
import by.dev.madhead.jarvis.util.createDefaultAddressesSet
import hudson.AbortException
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution

class JarvisStepExecution(
        context: StepContext,
        val defaultRecipients: String?) : SynchronousNonBlockingStepExecution<Void>(context) {

    override fun run(): Void? {
        val run = context.get(WorkflowRun::class.java)
                ?: throw AbortException(Messages.jarvis_hudson_AbortException_workflowRunRequired())
        val listener = context.get(TaskListener::class.java)
                ?: throw AbortException(Messages.jarvis_hudson_AbortException_taskListenerRequired())
        try {
            Jarvis().sendMail(
                    email = EmailCreator(run, listener,
                            context.get(FilePath::class.java)
                                    ?: throw AbortException(Messages.jarvis_hudson_AbortException_workspaceRequired())
                    ).create(),
                    defaultRecipientsAddresses = createDefaultAddressesSet(run, defaultRecipients),
                    logger = listener.logger)
            return null
        } catch (exception: Throwable) {
            throw AbortException(exception.message)
        }
    }

}
