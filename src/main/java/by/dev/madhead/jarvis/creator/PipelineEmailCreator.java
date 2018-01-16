package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.Change;
import by.dev.madhead.jarvis.model.Email;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PipelineEmailCreator extends EmailCreator {

    PipelineEmailCreator(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        super(run, workspace, launcher, listener);
    }

    @Override
    public Email create() throws IOException, InterruptedException {
        WorkflowRun run = (WorkflowRun) getRun();
        SCM scm = run.getParent().getTypicalSCM();
        if (scm != null) {
            EnvVars envVars = run.getEnvironment(getListener());
            scm.buildEnvironment(run, envVars);
            String gitUrl = findGitUrl(envVars);
            List<Change> changes = new ArrayList<>();
            run.getChangeSets().forEach(changeSet -> addChangesToList(gitUrl, changeSet, changes));
            run.setResult(run.getResult() == null ? Result.SUCCESS : run.getResult());
            return create(gitUrl, envVars, changes);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

}