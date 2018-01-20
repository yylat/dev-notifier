package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.Email;
import by.dev.madhead.jarvis.util.ChangesFiller;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;

import java.io.IOException;

public class ClassicEmailCreator extends EmailCreator {

    private AbstractBuild<?, ?> run;
    private BuildListener listener;

    ClassicEmailCreator(AbstractBuild<?, ?> run, BuildListener listener) {
        this.run = run;
        this.listener = listener;
    }

    @Override
    public Email create() throws IOException, InterruptedException {
        if (run.getParent().getScm() != null) {
            EnvVars envVars = run.getEnvironment(listener);
            String gitUrl = findGitUrl(envVars);
            return create(run, gitUrl, envVars, ChangesFiller.fillChangesList(gitUrl, run.getWorkspace(), run.getChangeSets()));
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

}