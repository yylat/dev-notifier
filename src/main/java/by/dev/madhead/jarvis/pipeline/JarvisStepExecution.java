package by.dev.madhead.jarvis.pipeline;

import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.creator.EmailCreatorFactory;
import by.dev.madhead.jarvis.util.AddressSearcher;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;

public class JarvisStepExecution extends SynchronousNonBlockingStepExecution {

    private final String defaultRecipients;

    JarvisStepExecution(@Nonnull StepContext context, String defaultRecipients) {
        super(context);
        this.defaultRecipients = defaultRecipients;
    }

    @Override
    protected Object run() throws Exception {
        WorkflowRun run = getContext().get(WorkflowRun.class);
        TaskListener listener = getContext().get(TaskListener.class);
        FilePath workspace = getContext().get(FilePath.class);
        if (run != null && listener != null && workspace != null) {
            Jarvis.INSTANCE.notify(
                    EmailCreatorFactory.getCreator(run, listener, workspace).create(),
                    Messages.jarvis_fromName(),
                    AddressSearcher.findBuilderAddress(run),
                    defaultRecipients);
        } else {
            throw new AbortException();
        }
        return null;
    }

}