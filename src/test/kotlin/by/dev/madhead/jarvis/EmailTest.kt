package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.Build
import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.model.Repo
import org.junit.jupiter.api.Test

class EmailTest {
	@Test
	fun send() {
		Jarvis.notify(Email(
				Repo(
						slug = "madhead/jarvis",
						branch = "master"
				),
				Build(
						number = 42
				)
		))
	}
}
