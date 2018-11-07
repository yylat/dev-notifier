package by.dev.madhead.devnotifier.creator

import by.dev.madhead.devnotifier.model.Author
import by.dev.madhead.devnotifier.model.Change
import by.dev.madhead.devnotifier.model.ChangeSet
import by.dev.madhead.devnotifier.util.findJenkinsUserAddressById
import hudson.FilePath
import hudson.scm.ChangeLogSet
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.io.IOException

fun fillChangesList(gitUrl: String, workspace: FilePath,
                    changeLogSets: List<ChangeLogSet<out ChangeLogSet.Entry>>): ChangeSet {
    val changeCreator = ChangeCreator(gitUrl, workspace)
    return changeLogSets.flatten().map { changeLogEntry -> changeCreator.create(changeLogEntry) }
}

class ChangeCreator(private val gitUrl: String, workspace: FilePath) {

    private val repository = RepositoryBuilder().setGitDir(File(workspace.remote + "/.git")).build()

    fun create(changeLogEntry: ChangeLogSet.Entry) =
            Change(
                    revision = changeLogEntry.commitId.substring(0, 7),
                    author = findAuthor(changeLogEntry.commitId) ?: Author(
                            username = changeLogEntry.author.id,
                            email = findJenkinsUserAddressById(changeLogEntry.author.id)),
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
