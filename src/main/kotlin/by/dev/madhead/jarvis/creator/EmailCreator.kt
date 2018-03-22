package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.model.*
import by.dev.madhead.jarvis.model.Build
import hudson.AbortException
import hudson.EnvVars
import hudson.FilePath
import hudson.model.*
import org.jenkinsci.plugins.workflow.job.WorkflowRun
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
                        status = defineBuildStatus(run),
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

    fun defineBuildStatus(run: Run<*, *>): BuildStatus {
        val currentResult = run.getResult()
        val previousResult = run.getPreviousBuild()?.getResult()

        return when (currentResult) {
            Result.SUCCESS ->
                when (previousResult) {
                    null, Result.SUCCESS -> BuildStatus.PASSED
                    else -> BuildStatus.FIXED
                }
            Result.UNSTABLE, Result.NOT_BUILT ->
                when (previousResult) {
                    null, Result.SUCCESS -> BuildStatus.BROKEN
                    Result.UNSTABLE, Result.NOT_BUILT -> BuildStatus.STILL_BROKEN
                    else -> BuildStatus.UNKNOWN
                }
            Result.FAILURE ->
                when (previousResult) {
                    Result.FAILURE -> BuildStatus.STILL_FAILING
                    else -> BuildStatus.FAILED
                }
            Result.ABORTED -> BuildStatus.ABORTED
            else -> BuildStatus.UNKNOWN
        }
    }

}
