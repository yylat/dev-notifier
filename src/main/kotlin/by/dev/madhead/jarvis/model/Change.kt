package by.dev.madhead.jarvis.model

data class Change(
		val revision: String,
		val author: String,
		val committer: String? = null,
		val message: String,
		val link: String? = null
)

typealias ChangeSet = List<Change>
