package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.model.*;
import by.dev.madhead.jarvis.util.AddressExtractor;
import by.dev.madhead.jarvis.util.BuildStatusQualifier;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.scm.ChangeLogSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public abstract class EmailCreator {

    private Run<?, ?> run;
    private FilePath workspace;
    private Launcher launcher;
    private TaskListener listener;

    Run<?, ?> getRun() {
        return run;
    }

    FilePath getWorkspace() {
        return workspace;
    }

    Launcher getLauncher() {
        return launcher;
    }

    TaskListener getListener() {
        return listener;
    }

    EmailCreator(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) {
        this.run = run;
        this.workspace = workspace;
        this.launcher = launcher;
        this.listener = listener;
    }

    public abstract Email create() throws IOException, InterruptedException;

    Email create(String gitUrl, EnvVars envVars, List<Change> changes) {
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
                        "jenkinsci-users@googlegroups.com"));
    }

    String findGitUrl(EnvVars envVars) {
        String gitUrl = envVars.get("GIT_URL");
        String suffix = ".git";
        return gitUrl.contains(suffix) ?
                gitUrl.substring(0, gitUrl.lastIndexOf(suffix)) : gitUrl;
    }

    List<Change> createChangesList(String gitUrl, ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet) {
        List<Change> changes = new ArrayList<>();
        addChangesToList(gitUrl, changeLogSet, changes);
        return changes;
    }

    void addChangesToList(String gitUrl, ChangeLogSet<? extends ChangeLogSet.Entry> changeLogSet, List<Change> changes) {
        changeLogSet.forEach(changeEntry -> changes.add(createChange(gitUrl, changeEntry)));
    }

    private Change createChange(String gitUrl, ChangeLogSet.Entry changeEntry) {
        Author author = findAuthor(changeEntry.getCommitId());
        if (author == null) {
            author = new Author(
                    changeEntry.getAuthor().getId(),
                    AddressExtractor.extract(changeEntry.getAuthor().getId()));
        }
        return new Change(
                changeEntry.getCommitId().substring(0, 7),
                author,
                changeEntry.getMsg(),
                gitUrl.concat("/commit/" + changeEntry.getCommitId()));
    }

    private Author findAuthor(String commitId) {
        Author author;
        try {
            String initCommand = launcher.isUnix() ? "sh -c" : "cmd /c";
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            launcher.launch()
                    .cmdAsSingleString(initCommand + " git show -s --format=\"%aN %ae\" " + commitId)
                    .stdout(out)
                    .pwd(workspace)
                    .start()
                    .join();
            String[] authorInfo = out.toString().trim().split(" ");
            author = new Author(authorInfo[0], authorInfo[1]);
        } catch (IOException | InterruptedException e) {
            author = null;
        }
        return author;
    }

}