package by.dev.madhead.jarvis

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailTest {
	companion object {
		lateinit var session: Session

		@BeforeAll
		@JvmStatic
		fun init() {
			session = Session.getDefaultInstance(
					Properties().apply {
						this["mail.smtp.host"] = System.getenv("JARVIS_SMTP_HOST")
						this["mail.smtp.port"] = System.getenv("JARVIS_SMTP_PORT")
						this["mail.smtp.auth"] = (!System.getenv("JARVIS_SMTP_USER").isNullOrBlank()).toString()
						this["mail.smtp.starttls.enable"] = System.getenv("JARVIS_SMTP_TLS")
					},
					if (!System.getenv("JARVIS_SMTP_USER").isNullOrBlank()) {
						object : Authenticator() {
							override fun getPasswordAuthentication(): PasswordAuthentication? {
								return PasswordAuthentication(
										System.getenv("JARVIS_SMTP_USER"),
										System.getenv("JARVIS_SMTP_PASSWORD")
								)
							}
						}
					} else {
						null
					}
			)
		}
	}

	@Test
	fun sendEmail() {
		val content = MimeMultipart().apply {
			addBodyPart(MimeBodyPart().apply {
				setContent("Hello, world!", "text/html; charset=utf-8")
			})
		}

		Transport.send(
				MimeMessage(session).apply {
					setFrom(InternetAddress(System.getenv("JARVIS_FROM")))
					System.getenv("JARVIS_TO").split(",", " ", ";").forEach {
						addRecipient(Message.RecipientType.TO, InternetAddress(it))
					}
					subject = LocalDateTime.now().toString()
					setContent(content)
				}
		)
	}
}
