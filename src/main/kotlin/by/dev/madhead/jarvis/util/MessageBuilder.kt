package by.dev.madhead.jarvis.util

import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.model.Email
import javax.mail.Address
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class MessageBuilder(val email: Email, val addresses: Set<Address>) {

    init {
        val recipientParser = RecipientParser()
        email.build.changeSet
                .mapNotNull { it.author.email }
                .forEach { recipientParser.addStringAsAddress(addresses, it) }
    }

    fun isMsgHasRecipients() = addresses.isNotEmpty()

    fun buildMessage(session: Session): MimeMessage {
        return MimeMessage(session).apply {
            subject = email.subject
            setFrom(InternetAddress(Messages.jarvis_fromName()))
            setRecipients(Message.RecipientType.TO, addresses.toTypedArray())
            setContent(ContentMaker().buildContent(email))
        }
    }

}
