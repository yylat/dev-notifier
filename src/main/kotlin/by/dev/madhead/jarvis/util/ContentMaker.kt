package by.dev.madhead.jarvis.util

import by.dev.madhead.jarvis.model.BuildStatus
import by.dev.madhead.jarvis.model.Email
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.util.*
import javax.activation.DataHandler
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class ContentMaker {

    private val pathToImages = "/by/dev/madhead/jarvis/images/"
    private val imagePng = "image/png"

    fun buildContent(email: Email): MimeMultipart {
        val engine = TemplateEngine().apply {
            setTemplateResolver(ClassLoaderTemplateResolver(ContentMaker::class.java.classLoader).apply {
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
        return MimeMultipart().apply {
            addBodyPart(MimeBodyPart().apply {
                setContent(engine.process("jarvis", context), "text/html: charset=utf-8")
            })
            addBodyPart(buildBodyPart("<status.png>", "Build status",
                    "${pathToImages}status-${image(email.build.status)}.png", imagePng))
            addBodyPart(buildBodyPart("<duration.png>", "Duration",
                    "${pathToImages}duration-${image(email.build.status)}.png", imagePng))
            addBodyPart(buildBodyPart("<jenkins.png>", "Jenkins", "${pathToImages}jenkins-${
            if ((email.build.status == BuildStatus.PASSED) || (email.build.status == BuildStatus.FIXED)) ""
            else "-in-fire"}.png", imagePng))
        }
    }

    private fun buildBodyPart(_contentID: String, _description: String,
                              resourceName: String, resourceType: String): MimeBodyPart {
        return MimeBodyPart().apply {
            contentID = _contentID
            description = _description
            disposition = """inline; filename="${contentID.replace(Regex("[<>]"), "")}""""
            dataHandler = DataHandler(ByteArrayDataSource(
                    ContentMaker::class.java.getResourceAsStream(resourceName), resourceType))
        }
    }

    private fun image(buildStatus: BuildStatus): String {
        return when (buildStatus) {
            BuildStatus.PASSED, BuildStatus.FIXED -> "success"
            BuildStatus.BROKEN, BuildStatus.FAILED, BuildStatus.STILL_BROKEN, BuildStatus.STILL_FAILING -> "failure"
            else -> "unknown"
        }
    }

}
