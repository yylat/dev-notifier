package by.dev.madhead.jarvis.pipeline;

import by.dev.madhead.jarvis.Jarvis;
import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.*;
import by.dev.madhead.jarvis.util.BuildStatusQualifier;
import hudson.AbortException;
import hudson.model.Run;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class JarvisStepExecution extends SynchronousNonBlockingStepExecution<Void> {

    private final String recipients;

    JarvisStepExecution(@Nonnull StepContext context, String recipients) {
        super(context);
        this.recipients = recipients;
    }

    @Override
    protected Void run() throws Exception {
        Run run = getContext().get(Run.class);
        if (run != null) {
            if (run instanceof WorkflowRun) {
                // TODO: find git url
                String gitUrl = "gitUrl";

                List<Change> changes = new ArrayList<>();
                ((WorkflowRun) run).getChangeSets().forEach(changeSet ->
                        changeSet.forEach(change -> changes.add(
                                new Change(
                                        change.getCommitId().substring(0, 7),
                                        "author",
                                        "committer",
                                        change.getMsg(),
                                        gitUrl.concat("/commit/" + change.getCommitId())
                                )
                        ))
                );

                // TODO: find "GIT_BRANCH", "GIT_COMMIT", "BUILD_URL"
                Jarvis.INSTANCE.notify(
                        new Email(
                                new Repo(
                                        gitUrl.substring(gitUrl.indexOf("/", gitUrl.indexOf(".com")) + 1),
                                        gitUrl),
                                new Build(
                                        run.getNumber(),
                                        "GIT_BRANCH",
                                        "GIT_COMMIT",
                                        BuildStatusQualifier.defineBuildStatus(run),
                                        Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                                        "BUILD_URL",
                                        changes),
                                new Extra(
                                        "support@travis-ci.com")),
                        "JARVIS",
                        recipients);

            }
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortExceprion_runRequired());
        }

        return null;
    }

}