package by.dev.madhead.jarvis.step;

import by.dev.madhead.jarvis.model.BuildStatus;
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
        PrintStream printStream = listener.getLogger();
        run.getEnvironment(listener).entrySet().forEach(printStream::println);
        printStream.println("Build result: " + run.getResult());
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