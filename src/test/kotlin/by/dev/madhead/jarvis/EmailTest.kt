package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.Build
import by.dev.madhead.jarvis.model.BuildStatus
import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.model.Repo
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.Random

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
fun ClosedRange<Long>.random(): Long = start + (Math.random() * (endInclusive - start)).toLong()
fun revision() = (0..6).map { (('a'..'f') + ('0'..'9')).let { it[Random().nextInt(it.size)] } }.joinToString(separator = "")

class EmailTest {
	@Test
	@Tag("base")
	fun sendPassed() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.PASSED,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendFixed() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.FIXED,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendBroken() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.BROKEN,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendStillBroken() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.STILL_BROKEN,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendFailed() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.FAILED,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendStillFailing() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.STILL_FAILING,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("extended")
	fun sendUnknown() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin",
						link = "https://github.com/jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						branch = "master",
						revision = revision(),
						status = BuildStatus.UNKNOWN,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}

	@Test
	@Tag("base")
	fun sendPassedMinimal() {
		Jarvis.notify(Email(
				Repo(
						slug = "jenkinsci/doktor-plugin"
				),
				Build(
						number = (1..99).random(),
						revision = revision(),
						status = BuildStatus.PASSED,
						duration = Duration.ofSeconds((30L..864000).random()),
						link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649"
				)
		))
	}
}
