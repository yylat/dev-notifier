package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.util.ChangesFiller
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
                    ChangesFiller().fillChangesList(gitUrl,
                            run.getWorkspace() ?:
                                    throw AbortException(Messages.jarvis_hudson_AbortException_workspaceRequired()),
                            run.getChangeSets()
                    )
            )
        } else throw AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM())
    }

}
