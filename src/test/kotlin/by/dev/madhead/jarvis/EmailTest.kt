package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.*
import hudson.tasks.Mailer
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.time.Duration
import java.util.Random

import org.powermock.api.mockito.PowerMockito.*

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start
fun ClosedRange<Long>.random(): Long = start + (Math.random() * (endInclusive - start)).toLong()
fun revision() = (0..6).map { (('a'..'f') + ('0'..'9')).let { it[Random().nextInt(it.size)] } }.joinToString(separator = "")

@RunWith(PowerMockRunner::class)
@PrepareForTest(Mailer::class)
@PowerMockIgnore("com.sun.mail.*", "javax.mail.*", "javax.activation.*")
class EmailTest {

    companion object {
        @JvmStatic
        val mailerDescriptor = mock<Mailer.DescriptorImpl>(Mailer.DescriptorImpl::class.java)

        @JvmStatic
        @BeforeClass
        fun setUp() {
            mockStatic(Mailer::class.java)

            `when`<Mailer.DescriptorImpl>(Mailer.descriptor()).thenReturn(mailerDescriptor)

            `when`(mailerDescriptor.smtpServer).thenReturn(System.getenv("JARVIS_SMTP_HOST"))
            `when`(mailerDescriptor.smtpPort).thenReturn(System.getenv("JARVIS_SMTP_PORT"))
            `when`(mailerDescriptor.smtpAuthUserName).thenReturn(System.getenv("JARVIS_SMTP_USER"))
            `when`(mailerDescriptor.smtpAuthPassword).thenReturn(System.getenv("JARVIS_SMTP_PASSWORD"))
        }
    }

    private val from = System.getenv("JARVIS_FROM")

    private val to = System.getenv("JARVIS_TO")

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                        supportEmail = "support@travis-ci.com"
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                        supportEmail = "support@travis-ci.com"
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = emptyList()
                ),
                Extra(
                        supportEmail = "support@travis-ci.com"
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                )
                        )
                ),
                Extra(
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix scripts",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                        supportEmail = "support@travis-ci.com"
                )
        ), from, to, null)
    }

    @Test
    fun sendAborted() {
        Jarvis.notify(Email(
                Repo(
                        slug = "jenkinsci/doktor-plugin",
                        link = "https://github.com/jenkinsci/doktor-plugin"
                ),
                Build(
                        number = (1..99).random(),
                        branch = "master",
                        revision = revision(),
                        status = BuildStatus.ABORTED,
                        duration = Duration.ofSeconds((30L..864000).random()),
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                        supportEmail = "support@travis-ci.com"
                )
        ), from, to, null)
    }

    @Test
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
                        link = "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                        changeSet = listOf(
                                Change(
                                        revision = "217c8a5",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "mv -> cp",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "WIP"
                                ),
                                Change(
                                        revision = "df156a6",
                                        author = Author(
                                                username = "madhead",
                                                email = to
                                        ),
                                        message = "Fix #35: move document output settings upper in hierarchy of calls",
                                        link = "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"
                                )
                        )
                ),
                Extra(
                )
        ), from, to, null)
    }
}