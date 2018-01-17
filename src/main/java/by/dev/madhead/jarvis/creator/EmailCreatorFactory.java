package by.dev.madhead.jarvis.creator;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

public final class EmailCreatorFactory {

    private EmailCreatorFactory() {
    }

    public static EmailCreator getClassic(AbstractBuild<?, ?> run, BuildListener listener, FilePath workspace) {
        return new ClassicEmailCreator(run, listener, workspace);
    }

    public static EmailCreator getPipeline(WorkflowRun run, TaskListener listener, FilePath workspace) {
        return new PipelineEmailCreator(run, listener, workspace);
    }

}