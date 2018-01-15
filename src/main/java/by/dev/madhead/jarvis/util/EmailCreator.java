package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.Messages;
import by.dev.madhead.jarvis.model.*;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.SCM;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public final class EmailCreator {

    private EmailCreator() {
    }

    public static Email create(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        Email email;
        if (run instanceof AbstractBuild) {
            email = classicCreate((AbstractBuild<?, ?>) run, workspace, launcher, listener);
        } else if (run instanceof WorkflowRun) {
            email = pipelineCreate((WorkflowRun) run, workspace, launcher, listener);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_unsupportedJob());
        }
        return email;
    }

    private static Email classicCreate(AbstractBuild<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        if (run.getParent().getScm() != null) {
            EnvVars envVars = run.getEnvironment(listener);
            String gitUrl = extractGitUrl(envVars);
            List<Change> changes = createChangesList(launcher, workspace, gitUrl, run.getChangeSet());
            return create(gitUrl, run, envVars, changes);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

    private static Email pipelineCreate(WorkflowRun run, FilePath workspace, Launcher launcher, TaskListener listener) throws IOException, InterruptedException {
        SCM scm = run.getParent().getTypicalSCM();
        if (scm != null) {
            EnvVars envVars = run.getEnvironment(listener);
            scm.buildEnvironment(run, envVars);
            String gitUrl = extractGitUrl(envVars);
            List<Change> changes = new ArrayList<>();
            run.getChangeSets().forEach(changeSet -> addChangesToList(launcher, workspace, gitUrl, changeSet, changes));
            run.setResult(run.getResult() == null ? Result.SUCCESS : run.getResult());
            return create(gitUrl, run, envVars, changes);
        } else {
            throw new AbortException(Messages.jarvis_hudson_AbortException_jobWithoutSCM());
        }
    }

    private static Email create(String gitUrl, Run run, EnvVars envVars, List<Change> changes) {
        return new Email(
                new Repo(
                        gitUrl.substring(gitUrl.indexOf("/", gitUrl.indexOf(".com")) + 1),
                        gitUrl),
                new Build(
                        run.getNumber(),
                        envVars.get("GIT_BRANCH").substring(envVars.get("GIT_BRANCH").lastIndexOf("/") + 1),
                        envVars.get("GIT_COMMIT").substring(0, 7),
                        BuildStatusQualifier.defineBuildStatus(run),
                        Duration.ofMillis(System.currentTimeMillis() - run.getTimeInMillis()),
                        envVars.get("BUILD_URL"),
                        changes),
                new Extra(
                        "jenkinsci-users@googlegroups.com")
        );
    }

    private static String extractGitUrl(EnvVars envVars) {
        String gitUrl = envVars.get("GIT_URL");
        String suffix = ".git";
        return gitUrl.contains(suffix) ?
                gitUrl.substring(0, gitUrl.lastIndexOf(suffix)) : gitUrl;
    }

    private static List<Change> createChangesList(Launcher launcher, FilePath workspace, String gitUrl, ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet) {
        List<Change> changes = new ArrayList<>();
        addChangesToList(launcher, workspace, gitUrl, changeLogSet, changes);
        return changes;
    }

    private static void addChangesToList(Launcher launcher, FilePath workspace, String gitUrl, ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet, List<Change> changes) {
        changeLogSet.forEach(changeEntry -> changes.add(createChange(launcher, workspace, gitUrl, changeEntry)));
    }

    private static Change createChange(Launcher launcher, FilePath workspace, String gitUrl, ChangeLogSet.Entry changeEntry) {
        String committer = extractCommitter(launcher, workspace, changeEntry.getCommitId());
        if (committer == null) {
            committer = changeEntry.getAuthor().getDisplayName();
        }
        return new Change(
                changeEntry.getCommitId().substring(0, 7),
                committer,
                committer,
                changeEntry.getMsg(),
                gitUrl.concat("/commit/" + changeEntry.getCommitId()));
    }

    private static String extractCommitter(Launcher launcher, FilePath workspace, String commitId) {
        String committer;
        try {
            String initCommand = launcher.isUnix() ? "sh -c" : "cmd /c";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            launcher.launch()
                    .cmdAsSingleString(initCommand + " git show -s --format=\"%aN\" " + commitId)
                    .stdout(out)
                    .pwd(workspace)
                    .start()
                    .join();
            committer = out.toString().trim();
        } catch (IOException | InterruptedException e) {
            committer = null;
        }
        return committer;
    }

}