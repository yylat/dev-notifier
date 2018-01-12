package by.dev.madhead.jarvis.step;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.util.EmailCreator;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;

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
        Jarvis.INSTANCE.notify(EmailCreator.create(run, listener), Messages.jarvis_step_JarvisStep_fromName(), recipients);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("jarvis")
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