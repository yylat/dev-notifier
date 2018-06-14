package by.dev.madhead.devnotifier.creator

import by.dev.madhead.devnotifier.model.Author
import by.dev.madhead.devnotifier.model.Change
import by.dev.madhead.devnotifier.model.ChangeSet
import by.dev.madhead.devnotifier.util.findJenkinsUserAddressById
import hudson.AbortException
import hudson.FilePath
import hudson.scm.ChangeLogSet
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.io.IOException

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

class ChangeCreator(
        private val gitUrl: String,
        workspace: FilePath) {

    private val repository = RepositoryBuilder()
            .setGitDir(File(workspace.remote + "/.git")).build()

    fun create(changeLogEntry: ChangeLogSet.Entry) =
            Change(
                    revision = changeLogEntry.commitId.substring(0, 7),
                    author = findAuthor(changeLogEntry.commitId) ?: Author(
                            changeLogEntry.author.id,
                            findJenkinsUserAddressById(changeLogEntry.author.id)),
                    message = changeLogEntry.msg,
                    link = "$gitUrl/commit/${changeLogEntry.commitId}"
            )


    private fun findAuthor(commitId: String): Author? {
        try {
            RevWalk(repository).use {
                val revCommit = it.parseCommit(ObjectId.fromString(commitId))
                return Author(revCommit.authorIdent.name, revCommit.authorIdent.emailAddress)
            }
        } catch (e: IOException) {
            return null
        }
    }

}
