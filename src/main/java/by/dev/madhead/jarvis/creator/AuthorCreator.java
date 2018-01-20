package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.model.Author;
import hudson.FilePath;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;

public class AuthorCreator implements AutoCloseable {

    private RevWalk revWalk;

    public AuthorCreator(FilePath workspace) throws IOException {
        this.revWalk = new RevWalk(new RepositoryBuilder().setGitDir(
                new File(workspace.getRemote() + "/.git")).build());
    }

    public Author find(String commitId) {
        try {
            RevCommit revCommit = revWalk.parseCommit(ObjectId.fromString(commitId));
            return new Author(revCommit.getAuthorIdent().getName(), revCommit.getAuthorIdent().getEmailAddress());
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {
        revWalk.close();
    }

}