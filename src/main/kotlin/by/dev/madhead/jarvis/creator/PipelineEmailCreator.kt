package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.util.ChangesFiller
import hudson.AbortException
import hudson.FilePath
import hudson.model.Result
import hudson.model.TaskListener
import org.jenkinsci.plugins.workflow.job.WorkflowRun

class PipelineEmailCreator(val run: WorkflowRun, val listener: TaskListener, val workspace: FilePath) : EmailCreator {

    override fun create(): Email {
        val scm = run.parent.typicalSCM
        if (scm != null) {
            val envVars = run.getEnvironment(listener)
            scm.buildEnvironment(run, envVars)
            val gitUrl = findGitUrl(envVars)
            run.setResult(run.result ?: Result.SUCCESS)
            return create(run, gitUrl, envVars, ChangesFiller().fillChangesList(gitUrl, workspace, run.changeSets))
        } else throw AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM())
    }

}
