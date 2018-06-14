package by.dev.madhead.devnotifier.model

data class Change(
        val revision: String,
        val author: Author,
        val message: String,
        val link: String? = null
)

typealias ChangeSet = List<Change>
