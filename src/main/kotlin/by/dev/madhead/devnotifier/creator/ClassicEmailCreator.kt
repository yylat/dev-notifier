package by.dev.madhead.devnotifier.creator

import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.model.Email
import hudson.AbortException
import hudson.model.AbstractBuild
import hudson.model.BuildListener

class ClassicEmailCreator(
        private val run: AbstractBuild<*, *>,
        private val listener: BuildListener) : EmailCreator {

    override fun create(): Email {
        if (run.getParent().getScm() != null) {
            val envVars = run.getEnvironment(listener)
            val gitUrl = findGitUrl(envVars)
            return create(run, gitUrl, envVars,
                    fillChangesList(gitUrl,
                            run.getWorkspace()
                                    ?: throw AbortException(Messages.devnotifier_hudson_AbortException_workspaceRequired()),
                            run.getChangeSets()
                    )
            )
        } else throw AbortException(Messages.devnotifier_hudson_AbortException_jobWithoutSCM())
    }

}
