package by.dev.madhead.jarvis.step;

import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.model.*;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.util.ArrayList;

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
        EnvVars envVars = run.getEnvironment(listener);

        Jarvis.INSTANCE.notify(
                new Email(
                        new Repo(
                                envVars.get("GIT_URL"),
                                envVars.get("GIT_URL")),
                        new Build(
                                run.getNumber(),
                                envVars.get("GIT_BRANCH"),
                                envVars.get("GIT_COMMIT").substring(0, 7),
                                defineBuildStatus(run.getResult(), run.getPreviousBuild() != null ? run.getPreviousBuild().getResult() : null),
                                Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                                envVars.get("BUILD_URL"),
                                new ArrayList<>()),
                        new Extra(
                                "support@travis-ci.com")),
                "JARVIS",
                recipients);
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

    @Symbol("jarvis")
    @Extension
    public static final class JarvisStepDescriptor extends BuildStepDescriptor<Publisher> {

        @Override
        public String getDisplayName() {
            return "Do jarvis";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }

}