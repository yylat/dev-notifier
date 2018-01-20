package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.model.*;
import by.dev.madhead.jarvis.util.BuildStatusQualifier;
import hudson.EnvVars;
import hudson.model.Run;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

/**
 * Provides base functionality for email object creation.
 */
public abstract class EmailCreator {

    /**
     * Creates email by defining all needed parameters for
     * {@link #create(Run, String, EnvVars, List)} method.
     *
     * @return email
     * @throws IOException          if can not load components for message
     * @throws InterruptedException if can not define environmental variables
     */
    public abstract Email create() throws IOException, InterruptedException;

    /**
     * Creates email.
     *
     * @param run     run of the build
     * @param gitUrl  url address of git repository
     * @param envVars environmental variables
     * @param changes list of changes
     * @return email
     */
    Email create(Run<?, ?> run, String gitUrl, EnvVars envVars, List<Change> changes) {
        return new Email(
                new Repo(
                        gitUrl.substring(gitUrl.indexOf("/", gitUrl.lastIndexOf(".")) + 1),
                        gitUrl),
                new Build(
                        run.getNumber(),
                        envVars.get("GIT_BRANCH").substring(envVars.get("GIT_BRANCH").lastIndexOf("/") + 1),
                        envVars.get("GIT_COMMIT").substring(0, 7),
                        BuildStatusQualifier.defineBuildStatus(run),
                        run.getDuration() > 0
                                ? Duration.ofMillis(run.getDuration())
                                : Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                        envVars.get("BUILD_URL"),
                        changes),
                new Extra(
                        "jenkinsci-users@googlegroups.com"));
    }

    String findGitUrl(EnvVars envVars) {
        String gitUrl = envVars.get("GIT_URL");
        String suffix = ".git";
        return gitUrl.contains(suffix)
                ? gitUrl.substring(0, gitUrl.lastIndexOf(suffix))
                : gitUrl;
    }

}