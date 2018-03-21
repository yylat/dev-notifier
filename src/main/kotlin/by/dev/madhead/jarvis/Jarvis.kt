package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.util.MessageBuilder
import hudson.tasks.Mailer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import javax.mail.*

class Jarvis {

    private val logger: Logger = LogManager.getLogger()

    private val session = fun(): Session {
        val mailerDescriptor = Mailer.descriptor()
        return Session.getDefaultInstance(
                Properties().apply {
                    this["mail.smtp.host"] = mailerDescriptor.smtpServer
                    this["mail.smtp.port"] = mailerDescriptor.smtpPort
                    this["mail.smtp.auth"] = mailerDescriptor.smtpAuthUserName
                    this["mail.smtp.ssl.enable"] = mailerDescriptor.useSsl
                },
                if (!mailerDescriptor.smtpAuthUserName.isNullOrBlank()) {
                    object : Authenticator() {
                        override fun getPasswordAuthentication(): PasswordAuthentication {
                            return PasswordAuthentication(
                                    mailerDescriptor.smtpAuthUserName,
                                    mailerDescriptor.smtpAuthPassword
                            )
                        }
                    }
                } else null
        )
    }()

    fun sendMail(email: Email, defaultRecipients: Set<Address>) {
        val messageBuilder = MessageBuilder(email, defaultRecipients)
        if (messageBuilder.isMsgHasRecipients())
            Transport.send(messageBuilder.buildMessage(session))
        else
            logger.log(Level.INFO, "No recipients to send email.")
    }

}