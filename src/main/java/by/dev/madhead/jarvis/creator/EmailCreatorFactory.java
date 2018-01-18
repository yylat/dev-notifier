package by.dev.madhead.jarvis.creator;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

public final class EmailCreatorFactory {

    private EmailCreatorFactory() {
    }

    public static EmailCreator getCreator(AbstractBuild<?, ?> run, BuildListener listener) {
        return new ClassicEmailCreator(run, listener);
    }

    public static EmailCreator getCreator(WorkflowRun run, TaskListener listener, FilePath workspace) {
        return new PipelineEmailCreator(run, listener, workspace);
    }

}