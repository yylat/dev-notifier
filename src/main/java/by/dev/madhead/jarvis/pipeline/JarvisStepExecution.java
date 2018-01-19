package by.dev.madhead.jarvis.pipeline;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.creator.EmailCreatorFactory;
import by.dev.madhead.jarvis.util.RecipientParser;
import hudson.AbortException;
import hudson.FilePath;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import java.io.IOException;

public class JarvisStepExecution extends SynchronousNonBlockingStepExecution {

    private final String defaultRecipients;
    private final boolean tlsEnable;

    JarvisStepExecution(@Nonnull StepContext context, String defaultRecipients, boolean tlsEnable) {
        super(context);
        this.defaultRecipients = defaultRecipients;
        this.tlsEnable = tlsEnable;
    }

    @Override
    protected Object run() throws Exception {
        WorkflowRun run = getContext().get(WorkflowRun.class);
        if (run == null) {
            throw new AbortException(Messages.jarvis_hudson_AbortException_workflowRunRequired());
        }
        TaskListener listener = getContext().get(TaskListener.class);
        if (listener == null) {
            throw new AbortException(Messages.jarvis_hudson_AbortException_taskListenerRequired());
        }
        FilePath workspace = getContext().get(FilePath.class);
        if (workspace == null) {
            throw new AbortException(Messages.jarvis_hudson_AbortException_workspaceRequired());
        }

        Jarvis jarvis = new Jarvis(tlsEnable);
        try {
            jarvis.sendMail(EmailCreatorFactory.getCreator(run, listener, workspace).create(),
                    RecipientParser.createDefaultAddressesSet(run, defaultRecipients));
        } catch (IOException | MessagingException e) {
            throw new AbortException(e.getMessage());
        }
        return null;
    }

}