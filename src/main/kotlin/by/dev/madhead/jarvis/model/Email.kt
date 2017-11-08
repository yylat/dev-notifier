package by.dev.madhead.jarvis.model

data class Email(
		val repo: Repo,
		val build: Build,
		val supportEmail: String? = null
) {
	val subject: String
		get() = """${build.status.forHumans}: ${repo.slug}#${build.number} (${if (build.branch.isNullOrBlank()) build.revision else "${build.branch} - ${build.revision}"})"""
}
