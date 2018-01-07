package by.dev.madhead.jarvis.step;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
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

    @DataBoundConstructor
    public JarvisStep() {
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        PrintStream printStream = listener.getLogger();
        run.getEnvironment(listener).entrySet().forEach(printStream::println);
        printStream.println("Build result: " + run.getResult());
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