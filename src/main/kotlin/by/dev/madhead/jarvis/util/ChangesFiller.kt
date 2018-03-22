package by.dev.madhead.jarvis.util

import by.dev.madhead.jarvis.creator.ChangeCreator
import by.dev.madhead.jarvis.model.Change
import by.dev.madhead.jarvis.model.ChangeSet
import hudson.AbortException
import hudson.FilePath
import hudson.scm.ChangeLogSet
import java.io.IOException

class ChangesFiller {

    fun fillChangesList(gitUrl: String, workspace: FilePath,
                        changeLogSets: List<ChangeLogSet<out ChangeLogSet.Entry>>): ChangeSet {
        val changes = mutableListOf<Change>()
        try {
            val changeCreator = ChangeCreator(gitUrl, workspace)
            changeLogSets.forEach { changeLogSet ->
                changeLogSet.forEach { changeLogEntry ->
                    changes.add(changeCreator.create(changeLogEntry))
                }
            }
        } catch (e: IOException) {
            throw AbortException(e.message)
        }
        return changes
    }

}
