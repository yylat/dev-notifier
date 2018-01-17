package by.dev.madhead.jarvis.classic;

import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.creator.EmailCreatorFactory;
import by.dev.madhead.jarvis.util.AddressSearcher;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;

public class JarvisStep extends Notifier {

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

    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        Jarvis.INSTANCE.notify(
                EmailCreatorFactory.getCreator(build, listener).create(),
                Messages.jarvis_fromName(),
                AddressSearcher.findBuilderAddress(build),
                defaultRecipients);
        return true;
    }

    @Extension
    public static final class JarvisStepDescriptor extends BuildStepDescriptor<Publisher> {

        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.jarvis_displayName();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        public FormValidation doCheckDefaultRecipients(@QueryParameter String defaultRecipients) {
            if (!defaultRecipients.isEmpty()) {
                for (String recipient : defaultRecipients.split("[;, ]")) {
                    if (!recipient.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]+$")) {
                        return FormValidation.error(Messages.jarvis_emailValidation());
                    }
                }
            }
            return FormValidation.ok();
        }

    }

}