package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.Messages;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

public final class EmailCreatorFactory {

    private EmailCreatorFactory() {
    }

    public static EmailCreator getCreator(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws AbortException {
        if (run instanceof AbstractBuild) {
            return new ClassicEmailCreator(run, workspace, launcher, listener);
        } else if (run instanceof WorkflowRun) {
            return new PipelineEmailCreator(run, workspace, launcher, listener);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_unsupportedJob());
        }
    }

}