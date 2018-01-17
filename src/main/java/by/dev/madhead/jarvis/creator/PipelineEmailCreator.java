package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.Email;
import by.dev.madhead.jarvis.util.ChangesFiller;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.Result;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;

public class PipelineEmailCreator extends EmailCreator {

    private WorkflowRun run;
    private TaskListener listener;
    private FilePath workspace;

    PipelineEmailCreator(WorkflowRun run, TaskListener listener, FilePath workspace) {
        this.run = run;
        this.listener = listener;
        this.workspace = workspace;
    }

    @Override
    public Email create() throws IOException, InterruptedException {
        SCM scm = run.getParent().getTypicalSCM();
        if (scm != null) {
            EnvVars envVars = run.getEnvironment(listener);
            scm.buildEnvironment(run, envVars);
            String gitUrl = findGitUrl(envVars);
            run.setResult(run.getResult() == null ? Result.SUCCESS : run.getResult());
            return create(run, gitUrl, envVars, ChangesFiller.fillChangesList(gitUrl, workspace, run.getChangeSets()));
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

}