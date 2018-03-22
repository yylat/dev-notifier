package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.util.BuildStatusQualifier
import by.dev.madhead.jarvis.model.*
import hudson.AbortException
import hudson.EnvVars
import hudson.model.Run
import hudson.model.TaskListener
import java.time.Duration

interface EmailCreator {

    fun create(): Email

    fun create(run: Run<*, *>, gitUrl: String, envVars: EnvVars, changes: ChangeSet): Email {
        return Email(
                repo = Repo(
                        slug = gitUrl.substring(gitUrl.indexOf("/", gitUrl.lastIndexOf(".")) + 1),
                        link = gitUrl
                ),
                build = Build(
                        number = run.number,
                        branch = envVars.get("GIT_BRANCH")?.substring(envVars.get("GIT_BRANCH")!!.lastIndexOf("/") + 1),
                        revision = envVars.get("GIT_COMMIT")?.substring(0, 7) ?:
                                throw AbortException("Can not find git repository commits info."),
                        status = BuildStatusQualifier().defineBuildStatus(run),
                        duration = if (run.getDuration() > 0)
                            Duration.ofMillis(run.getDuration()) else
                            Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                        link = envVars.get("BUILD_URL") ?: throw AbortException("Can not find build url."),
                        changeSet = changes
                ),
                extra = Extra(
                        supportEmail = "jenkinsci-users@googlegroups.com"
                )
        )
    }

    fun findGitUrl(envVars: EnvVars): String {
        val gitUrl = envVars.get("GIT_URL") ?: throw AbortException("Can not find git repository url.")
        val suffix = ".git"
        return if (gitUrl.contains(suffix, false))
            gitUrl.substring(0, gitUrl.lastIndexOf(suffix)) else gitUrl
    }

}
