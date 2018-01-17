package by.dev.madhead.jarvis.step;

import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.creator.EmailCreatorFactory;
import by.dev.madhead.jarvis.util.AddressExtractor;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class JarvisStep extends Notifier implements SimpleBuildStep {

    private final String defaultRecipients;

    @DataBoundConstructor
    public JarvisStep(String defaultRecipients) {
        if (defaultRecipients.isEmpty()) {
            this.defaultRecipients = null;
        } else {
            this.defaultRecipients = defaultRecipients;
        }
    }

    public String getDefaultRecipients() {
        return defaultRecipients;
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath workspace,
                        @Nonnull Launcher launcher, @Nonnull TaskListener listener) throws IOException, InterruptedException {
        Jarvis.INSTANCE.notify(
                EmailCreatorFactory.getCreator(run, workspace, launcher, listener).create(),
                Messages.jarvis_step_JarvisStep_fromName(),
                AddressExtractor.extractBuilderAddress(run),
                defaultRecipients);
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Symbol("jarvis")
    @Extension
    public static final class JarvisStepDescriptor extends BuildStepDescriptor<Publisher> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.jarvis_step_JarvisStep_displayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public FormValidation doCheckDefaultRecipients(@QueryParameter String defaultRecipients) {
            if (!defaultRecipients.isEmpty()) {
                for (String recipient : defaultRecipients.split("[;, ]")) {
                    if (!recipient.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]+$")) {
                        return FormValidation.error(Messages.jarvis_step_JarvisStep_emailValidation());
                    }
                }
            }
            return FormValidation.ok();
        }

    }

}