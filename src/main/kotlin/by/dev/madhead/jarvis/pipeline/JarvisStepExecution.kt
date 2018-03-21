package by.dev.madhead.jarvis.pipeline

import by.dev.madhead.jarvis.Jarvis
import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.creator.EmailCreatorFactory
import by.dev.madhead.jarvis.util.RecipientParser
import hudson.AbortException
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution
import java.io.IOException
import javax.mail.MessagingException

class JarvisStepExecution(context: StepContext, val defaultRecipients: String) : SynchronousNonBlockingStepExecution<Void>(context) {

    override fun run(): Void? {
        val run = context.get(WorkflowRun::class.java) ?:
                throw AbortException(Messages.jarvis_hudson_AbortException_workflowRunRequired())
        try {
            Jarvis().sendMail(
                    email = EmailCreatorFactory().getCreator(run,
                            context.get(TaskListener::class.java) ?:
                                    throw AbortException(Messages.jarvis_hudson_AbortException_taskListenerRequired()),
                            context.get(FilePath::class.java) ?:
                                    throw AbortException(Messages.jarvis_hudson_AbortException_workspaceRequired())
                    ).create(),
                    defaultRecipients = RecipientParser().createDefaultAddressesSet(run, defaultRecipients))
        } catch (eIO: IOException) {
            throw AbortException(eIO.message)
        } catch (eMessaging: MessagingException) {
            throw AbortException(eMessaging.message)
        }
        return null
    }

}
