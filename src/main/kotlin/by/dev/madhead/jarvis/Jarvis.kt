package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.BuildStatus
import by.dev.madhead.jarvis.model.Email
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.*
import javax.activation.DataHandler
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource
import hudson.tasks.Mailer

object Jarvis {

    private val session: Session by lazy {
        val mailerDescriptor = Mailer.descriptor()
        Session.getDefaultInstance(
                Properties().apply {
                    this["mail.smtp.host"] = mailerDescriptor.smtpServer
                    this["mail.smtp.port"] = mailerDescriptor.smtpPort
                    this["mail.smtp.auth"] = (!mailerDescriptor.smtpAuthUserName.isNullOrBlank()).toString()
                    this["mail.smtp.starttls.enable"] = "true"
                },
                if (!mailerDescriptor.smtpAuthUserName.isNullOrBlank()) {
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication? {
                            return PasswordAuthentication(
                                    mailerDescriptor.smtpAuthUserName,
                                    mailerDescriptor.smtpAuthPassword
                            )
                        }
                    }
                } else {
                    null
                }
        )
    }

    fun notify(email: Email, from: String, to: String) {
        val engine = TemplateEngine().apply {
            setTemplateResolver(ClassLoaderTemplateResolver(Jarvis::class.java.classLoader).apply {
                templateMode = TemplateMode.HTML
                characterEncoding = Charsets.UTF_8.name()
                prefix = "/by/dev/madhead/jarvis/templates/"
                suffix = ".html"
                isCacheable = true
            })
        }
        val context = Context().apply {
            setVariable("email", email)
            setVariable("randomSuccessExhortation", Random().nextInt(4).toString(10))
            setVariable("randomFailureExhortation", Random().nextInt(2).toString(10))
        }
        val content = MimeMultipart().apply {
            addBodyPart(MimeBodyPart().apply {
                setContent(engine.process("jarvis", context), "text/html; charset=utf-8")
            })
            addBodyPart(MimeBodyPart().apply {
                contentID = "<status.png>"
                description = "Build status"
                disposition = """inline; filename="${contentID.replace(Regex("[<>]"), "")}""""
                dataHandler = DataHandler(ByteArrayDataSource(Jarvis::class.java.getResourceAsStream("/by/dev/madhead/jarvis/images/status-${image(email.build.status)}.png"), "image/png"))
            })
            addBodyPart(MimeBodyPart().apply {
                contentID = "<duration.png>"
                description = "Duration"
                disposition = """inline; filename="${contentID.replace(Regex("[<>]"), "")}""""
                dataHandler = DataHandler(ByteArrayDataSource(Jarvis::class.java.getResourceAsStream("/by/dev/madhead/jarvis/images/duration-${image(email.build.status)}.png"), "image/png"))
            })
            addBodyPart(MimeBodyPart().apply {
                contentID = "<jenkins.png>"
                description = "Jenkins"
                disposition = """inline; filename="${contentID.replace(Regex("[<>]"), "")}""""
                dataHandler = DataHandler(ByteArrayDataSource(Jarvis::class.java.getResourceAsStream("/by/dev/madhead/jarvis/images/jenkins${if ((email.build.status == BuildStatus.PASSED) || (email.build.status == BuildStatus.FIXED)) "" else "-in-fire"}.png"), "image/png"))
            })
        }

        Transport.send(
                MimeMessage(session).apply {
                    setFrom(InternetAddress(from))
                    to.split(",", " ", ";").forEach {
                        addRecipient(Message.RecipientType.TO, InternetAddress(it))
                    }
                    subject = email.subject
                    setContent(content)
                }
        )
    }

    private fun image(buildStatus: BuildStatus): String {
        return when (buildStatus) {
            BuildStatus.PASSED, BuildStatus.FIXED -> "success"
            BuildStatus.BROKEN, BuildStatus.STILL_BROKEN, BuildStatus.FAILED, BuildStatus.STILL_FAILING -> "failure"
            BuildStatus.UNKNOWN, BuildStatus.ABORTED -> "unknown"
        }
    }
}
