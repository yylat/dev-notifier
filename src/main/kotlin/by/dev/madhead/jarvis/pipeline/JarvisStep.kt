package by.dev.madhead.jarvis.pipeline

import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.util.isValidAddresses
import hudson.AbortException
import hudson.Extension
import hudson.FilePath
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun
import org.jenkinsci.plugins.workflow.steps.Step
import org.jenkinsci.plugins.workflow.steps.StepContext
import org.jenkinsci.plugins.workflow.steps.StepDescriptor
import org.jenkinsci.plugins.workflow.steps.StepExecution
import org.kohsuke.stapler.DataBoundConstructor

class JarvisStep
@DataBoundConstructor
constructor(val defaultRecipients: String?) : Step() {

    override fun start(context: StepContext): StepExecution {
        if (isValidAddresses(defaultRecipients))
            return JarvisStepExecution(context, defaultRecipients)
        else
            throw AbortException(Messages.jarvis_validation_email())
    }

    @Extension
    class JarvisStepDescriptor : StepDescriptor() {
        override fun getFunctionName(): String = Messages.jarvis_functionName()

        override fun getRequiredContext() = setOf(
                WorkflowRun::class.java,
                TaskListener::class.java,
                FilePath::class.java)
    }

}
