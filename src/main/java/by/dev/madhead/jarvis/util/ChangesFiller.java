package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.creator.ChangeCreator;
import by.dev.madhead.jarvis.model.Change;
import hudson.AbortException;
import hudson.FilePath;
import hudson.scm.ChangeLogSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChangesFiller {

    private ChangesFiller() {
    }

    public static List<Change> fillChangesList(String gitUrl, FilePath workspace, List<ChangeLogSet<? extends ChangeLogSet.Entry>> changeLogSets) throws AbortException {
        List<Change> changes = new ArrayList<>();
        try {
            ChangeCreator changeCreator = new ChangeCreator(gitUrl, workspace);
            changeLogSets.forEach(changeLogSet ->
                    changeLogSet.forEach(changeLogEntry -> changes.add(changeCreator.create(changeLogEntry))));
        } catch (IOException e) {
            throw new AbortException(e.getMessage());
        }
        return changes;
    }

}