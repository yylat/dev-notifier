package by.dev.madhead.jarvis.creator;

import by.dev.madhead.jarvis.model.Author;
import by.dev.madhead.jarvis.model.Change;
import by.dev.madhead.jarvis.util.AddressSearcher;
import hudson.FilePath;
import hudson.scm.ChangeLogSet;

import java.io.IOException;

public class ChangeCreator implements AutoCloseable {

    private final String gitUrl;
    private final AuthorCreator authorCreator;

    public ChangeCreator(String gitUrl, FilePath workspace) throws IOException {
        this.gitUrl = gitUrl;
        this.authorCreator = new AuthorCreator(workspace);
    }

    public Change create(ChangeLogSet.Entry changeLogEntry) {
        Author author = authorCreator.find(changeLogEntry.getCommitId());
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


    @Override
    public void close() {
        authorCreator.close();
    }

}