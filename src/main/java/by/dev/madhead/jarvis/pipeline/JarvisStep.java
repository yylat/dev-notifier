package by.dev.madhead.jarvis.pipeline;

import by.dev.madhead.jarvis.Messages;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.HashSet;
import java.util.Set;

public class JarvisStep extends Step {

    private final String defaultRecipients;

    @DataBoundConstructor
    public JarvisStep(String defaultRecipients) {
        this.defaultRecipients = defaultRecipients;
    }

    public String getDefaultRecipients() {
        return defaultRecipients;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new JarvisStepExecution(context, defaultRecipients);
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