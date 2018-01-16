package by.dev.madhead.jarvis.model

data class Change(
		val revision: String,
		val author: Author,
		val message: String,
		val link: String? = null
)

typealias ChangeSet = List<Change>
