package by.dev.madhead.jarvis.model

data class Email(
		val repo: Repo,
		val build: Build
) {
	val subject: String
		get() = """${build.status.forHumans}: ${repo.slug}#${build.number} (${if (build.branch.isNullOrBlank()) build.revision else "${build.branch} - ${build.revision}"})"""
}
