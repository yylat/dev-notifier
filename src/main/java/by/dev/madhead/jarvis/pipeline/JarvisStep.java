package by.dev.madhead.jarvis.pipeline;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.util.RecipientParser;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents email notification step for pipelines.
 */
public class JarvisStep extends Step {

    private final String defaultRecipients;
    private boolean tlsEnable;

    @DataBoundConstructor
    public JarvisStep(String defaultRecipients) {
        this.defaultRecipients = defaultRecipients;
        this.tlsEnable = true;
    }

    @DataBoundSetter
    public void setTlsEnable(boolean tlsEnable) {
        this.tlsEnable = tlsEnable;
    }

    public String getDefaultRecipients() {
        return defaultRecipients;
    }

    public boolean isTlsEnable() {
        return tlsEnable;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        if (RecipientParser.isValidAddresses(defaultRecipients)) {
            return new JarvisStepExecution(context, defaultRecipients, tlsEnable);
        } else {
            throw new AbortException(Messages.jarvis_validation_email());
        }
    }

    @Extension
    public static final class JarvisStepDescriptor extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> requiredContext = new HashSet<>();
            requiredContext.add(WorkflowRun.class);
            requiredContext.add(TaskListener.class);
            requiredContext.add(FilePath.class);
            return requiredContext;
        }

        @Override
        public String getFunctionName() {
            return Messages.jarvis_functionName();
        }
    }

}