package by.dev.madhead.devnotifier.creator

import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.model.Email
import hudson.AbortException
import hudson.FilePath
import hudson.model.Result
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class PipelineEmailCreator(
        private val run: WorkflowRun,
        private val listener: TaskListener,
        private val workspace: FilePath) : EmailCreator {

    override fun create(): Email {
        val scm = run.parent.typicalSCM
        if (scm != null) {
            val envVars = run.getEnvironment(listener)
            scm.buildEnvironment(run, envVars)
            val gitUrl = findGitUrl(envVars)
            run.setResult(run.result ?: Result.SUCCESS)
            return create(run, gitUrl, envVars, fillChangesList(gitUrl, workspace, run.changeSets))
        } else throw AbortException(Messages.devnotifier_hudson_AbortException_jobWithoutSCM())

    }

}
