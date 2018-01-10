package by.dev.madhead.jarvis.step;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.model.*;
import by.dev.madhead.jarvis.model.Build;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class JarvisStep extends Notifier implements SimpleBuildStep {

    private final String recipients;

    @DataBoundConstructor
    public JarvisStep(String recipients) {
        this.recipients = recipients;
    }

    public String getRecipients() {
        return recipients;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        if (AbstractBuild.class == run.getClass()) {
            EnvVars envVars = run.getEnvironment(listener);
            final String gitUrl = envVars.get("GIT_URL").substring(0, envVars.get("GIT_URL").lastIndexOf(".git"));

            // TODO: find committer info
            List<Change> changes = new ArrayList<>();
            ((AbstractBuild<?, ?>) run).getChangeSet().forEach(change ->
                    changes.add(
                            new Change(
                                    change.getCommitId().substring(0, 7),
                                    "author",
                                    "committer",
                                    change.getMsg(),
                                    gitUrl.concat("/commit/" + change.getCommitId())
                            )
                    )
            );

            Jarvis.INSTANCE.notify(
                    new Email(
                            new Repo(
                                    gitUrl.substring(gitUrl.indexOf("/", gitUrl.indexOf(".com")) + 1),
                                    gitUrl),
                            new Build(
                                    run.getNumber(),
                                    envVars.get("GIT_BRANCH").substring(envVars.get("GIT_BRANCH").lastIndexOf("/") + 1),
                                    envVars.get("GIT_COMMIT").substring(0, 7),
                                    defineBuildStatus(run.getResult(), run.getPreviousBuild() != null ? run.getPreviousBuild().getResult() : null),
                                    Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                                    envVars.get("BUILD_URL"),
                                    changes),
                            new Extra(
                                    "support@travis-ci.com")),
                    "JARVIS",
                    recipients);
        }
    }

    private BuildStatus defineBuildStatus(Result currentResult, Result previousResult) {
        if (Result.SUCCESS.equals(currentResult)
                && (previousResult == null || Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.PASSED;
        }

        if (Result.SUCCESS.equals(currentResult)
                && (previousResult != null && !Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.FIXED;
        }

        if ((Result.UNSTABLE.equals(currentResult) || Result.NOT_BUILT.equals(currentResult))
                && (previousResult == null || Result.SUCCESS.equals(previousResult))) {
            return BuildStatus.BROKEN;
        }

        if ((Result.UNSTABLE.equals(currentResult) || Result.NOT_BUILT.equals(currentResult))
                && (previousResult != null
                && (Result.UNSTABLE.equals(previousResult) || Result.NOT_BUILT.equals(previousResult)))) {
            return BuildStatus.STILL_BROKEN;
        }

        if (Result.FAILURE.equals(currentResult)
                && (previousResult == null || !Result.FAILURE.equals(previousResult))) {
            return BuildStatus.FAILED;
        }

        if (Result.FAILURE.equals(currentResult)
                && (previousResult != null && Result.FAILURE.equals(previousResult))) {
            return BuildStatus.STILL_FAILING;
        }

        if (Result.ABORTED.equals(currentResult)) {
            return BuildStatus.ABORTED;
        }

        return BuildStatus.UNKNOWN;
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class JarvisStepDescriptor extends BuildStepDescriptor<Publisher> {

        @NotNull
        @Override
        public String getDisplayName() {
            return Messages.jarvis_step_JarvisStep_displayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }

}