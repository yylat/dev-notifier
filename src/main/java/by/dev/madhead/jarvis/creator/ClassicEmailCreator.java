package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.Change;
import by.dev.madhead.jarvis.model.Email;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Run;
import hudson.model.TaskListener;

import java.io.IOException;
import java.util.List;

public class ClassicEmailCreator extends EmailCreator {

    ClassicEmailCreator(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        super(run, workspace, launcher, listener);
    }

    @Override
    public Email create() throws IOException, InterruptedException {
        AbstractBuild<?, ?> run = (AbstractBuild<?, ?>) getRun();
        if (run.getParent().getScm() != null) {
            EnvVars envVars = run.getEnvironment(getListener());
            String gitUrl = findGitUrl(envVars);
            List<Change> changes = createChangesList(gitUrl, run.getChangeSet());
            return create(gitUrl, envVars, changes);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

}