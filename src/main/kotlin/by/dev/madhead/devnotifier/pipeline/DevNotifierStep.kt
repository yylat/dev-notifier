package by.dev.madhead.devnotifier.pipeline

import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.util.isValidAddresses
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

class DevNotifierStep
@DataBoundConstructor
constructor(val defaultRecipients: String?) : Step() {

    override fun start(context: StepContext): StepExecution {
        if (isValidAddresses(defaultRecipients))
            return DevNotifierStepExecution(context, defaultRecipients)
        else
            throw AbortException(Messages.devnotifier_validation_email())
    }

    @Extension
    class DevNotifierStepDescriptor : StepDescriptor() {
        override fun getFunctionName(): String = Messages.devnotifier_functionName()

        override fun getRequiredContext() = setOf(
                WorkflowRun::class.java,
                TaskListener::class.java,
                FilePath::class.java)
    }

}
