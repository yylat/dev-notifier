package by.dev.madhead.jarvis.pipeline;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.HashSet;
import java.util.Set;

public class JarvisStep extends Step {

    private final String recipients;

    @DataBoundConstructor
    public JarvisStep(String recipients) {
        this.recipients = recipients;
    }

    public String getRecipients() {
        return recipients;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new JarvisStepExecution(context, recipients);
    }

    @Extension
    public static class JarvisStepDescriptor extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            Set<Class<?>> requiredContext = new HashSet<>();
            requiredContext.add(Run.class);
            requiredContext.add(TaskListener.class);
            return requiredContext;
        }

        @Override
        public String getFunctionName() {
            return "jarvis";
        }

    }

}