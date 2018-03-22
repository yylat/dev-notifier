package by.dev.madhead.jarvis.creator

import by.dev.madhead.jarvis.model.Author
import by.dev.madhead.jarvis.model.Change
import by.dev.madhead.jarvis.util.AddressSearcher
import hudson.FilePath
import hudson.scm.ChangeLogSet
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.RepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import java.io.IOException

class ChangeCreator(
        private val gitUrl: String,
        workspace: FilePath) {

    private val repository = RepositoryBuilder()
            .setGitDir(File(workspace.remote + "/.git")).build()

    fun create(changeLogEntry: ChangeLogSet.Entry): Change {
        return Change(
                revision = changeLogEntry.commitId.substring(0, 7),
                author = findAuthor(changeLogEntry.commitId) ?: Author(
                        changeLogEntry.author.id,
                        AddressSearcher().findById(changeLogEntry.author.id)),
                message = changeLogEntry.msg,
                link = "$gitUrl/commit/${changeLogEntry.commitId}"
        )
    }

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
