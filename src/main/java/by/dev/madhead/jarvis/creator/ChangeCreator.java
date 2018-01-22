package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.model.Author;
import by.dev.madhead.jarvis.model.Change;
import by.dev.madhead.jarvis.util.AddressSearcher;
import hudson.FilePath;
import hudson.scm.ChangeLogSet;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;

public class ChangeCreator {

    private final String gitUrl;
    private Repository repository;

    public ChangeCreator(String gitUrl, FilePath workspace) throws IOException {
        this.gitUrl = gitUrl;
        this.repository = new RepositoryBuilder().setGitDir(
                new File(workspace.getRemote() + "/.git")).build();
    }

    public Change create(ChangeLogSet.Entry changeLogEntry) {
        Author author = find(changeLogEntry.getCommitId());
        if (author == null) {
            author = new Author(
                    changeLogEntry.getAuthor().getId(),
                    AddressSearcher.findById(changeLogEntry.getAuthor().getId()));
        }
        return new Change(
                changeLogEntry.getCommitId().substring(0, 7),
                author,
                changeLogEntry.getMsg(),
                gitUrl.concat("/commit/" + changeLogEntry.getCommitId()));
    }

    private Author find(String commitId) {
        try (RevWalk revWalk = new RevWalk(repository)) {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commitId));
            return new Author(revCommit.getAuthorIdent().getName(), revCommit.getAuthorIdent().getEmailAddress());
        } catch (IOException e) {
            return null;
        }
    }

}