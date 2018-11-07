package by.dev.madhead.devnotifier.util

import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.model.BuildStatus
import by.dev.madhead.devnotifier.model.Email
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.*
import javax.activation.DataHandler
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class MessageBuilder(private val email: Email, defaultRecipientsAddresses: Set<Address>) {

    private val allRecipientAddresses = defaultRecipientsAddresses
            .union(email.build.changeSet.mapNotNull { it.author.email }.map { stringToAddress(it) })

    fun isMsgHasRecipients() = allRecipientAddresses.isNotEmpty()

    fun recipientsInfo() = allRecipientAddresses.joinToString(", ")

    fun buildMessage(session: Session): MimeMessage {
        return MimeMessage(session).apply {
            subject = email.subject
            setFrom(InternetAddress(Messages.devnotifier_fromName()))
            setRecipients(Message.RecipientType.TO, allRecipientAddresses.toTypedArray())
            setContent(buildContent(email))
        }
    }

    private fun buildContent(email: Email): MimeMultipart {
        val pathToImages = "/by/dev/madhead/devnotifier/images/"
        val imagePng = "image/png"
        val engine = TemplateEngine().apply {
            setTemplateResolver(ClassLoaderTemplateResolver(MessageBuilder::class.java.classLoader).apply {
                templateMode = TemplateMode.HTML
                characterEncoding = Charsets.UTF_8.name()
                prefix = "/by/dev/madhead/devnotifier/templates/"
                suffix = ".html"
                isCacheable = true
            })
        }
        val context = Context().apply {
            setVariable("email", email)
            setVariable("randomSuccessExhortation", Random().nextInt(4).toString(10))
            setVariable("randomFailureExhortation", Random().nextInt(2).toString(10))
        }
        return MimeMultipart().apply {
            addBodyPart(MimeBodyPart().apply {
                setContent(engine.process("devnotifier", context), "text/html; charset=utf-8")
            })
            addBodyPart(buildBodyPart("<status.png>", "Build status",
                    "${pathToImages}status-${imageSuffix(email.build.status)}.png", imagePng))
            addBodyPart(buildBodyPart("<duration.png>", "Duration",
                    "${pathToImages}duration-${imageSuffix(email.build.status)}.png", imagePng))
            addBodyPart(buildBodyPart("<jenkins.png>", "Jenkins", "${pathToImages}jenkins${
            if ((email.build.status == BuildStatus.PASSED) || (email.build.status == BuildStatus.FIXED)) ""
            else "-in-fire"}.png", imagePng))
        }
    }

    private fun buildBodyPart(_contentID: String, _description: String,
                              resourceName: String, resourceType: String) =
            MimeBodyPart().apply {
                contentID = _contentID
                description = _description
                disposition = """inline; filename="${contentID.replace(Regex("[<>]"), "")}""""
                dataHandler = DataHandler(ByteArrayDataSource(
                        MessageBuilder::class.java.getResourceAsStream(resourceName), resourceType))
            }


    private fun imageSuffix(buildStatus: BuildStatus) =
            when (buildStatus) {
                BuildStatus.PASSED, BuildStatus.FIXED -> "success"
                BuildStatus.BROKEN, BuildStatus.FAILED, BuildStatus.STILL_BROKEN, BuildStatus.STILL_FAILING -> "failure"
                else -> "unknown"
            }

}
