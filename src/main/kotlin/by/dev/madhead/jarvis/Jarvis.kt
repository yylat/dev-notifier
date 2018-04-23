package by.dev.madhead.jarvis

import by.dev.madhead.jarvis.model.Email
import by.dev.madhead.jarvis.util.MessageBuilder
import hudson.AbortException
import hudson.tasks.Mailer
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.io.PrintStream
import java.util.*
import javax.mail.*

class Jarvis {

    private val session = fun(): Session {
        val mailerDescriptor = Mailer.descriptor()
        return Session.getDefaultInstance(
                Properties().apply {
                    this["mail.smtp.host"] = mailerDescriptor.smtpServer
                    this["mail.smtp.port"] = mailerDescriptor.smtpPort
                    this["mail.smtp.auth"] = (!mailerDescriptor.smtpAuthUserName.isNullOrBlank()).toString()
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
                } else {
                    null
                }
        )
    }()

    fun sendMail(email: Email, defaultRecipientsAddresses: Set<Address>, logger: PrintStream) {
        val messageBuilder = MessageBuilder(email, defaultRecipientsAddresses)
        if (messageBuilder.isMsgHasRecipients()) {
            try {
                Transport.send(messageBuilder.buildMessage(session))
                logger.println("Emails sent to: ${messageBuilder.recipientsInfo()}")
            } catch (e: MessagingException) {
                handleException(logger, e)
            } catch (e: IOException) {
                handleException(logger, e)
            }
        } else {
            logger.println("No recipients to send email.")
        }
    }

    private fun handleException(jenkinsLogger: PrintStream, e: Exception) {
        jenkinsLogger.println("Error while sending email.")
        LogManager.getLogger().log(Level.ERROR, e.stackTrace)
        throw AbortException("${e.cause}: ${e.message}")
    }

}