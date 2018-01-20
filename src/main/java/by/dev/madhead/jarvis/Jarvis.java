package by.dev.madhead.jarvis;

import by.dev.madhead.jarvis.model.Email;
import by.dev.madhead.jarvis.util.MessageBuilder;
import hudson.tasks.Mailer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class Jarvis {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Session session;

    public Jarvis(boolean tlsEnable) {
        Mailer.DescriptorImpl mailerDescriptor = Mailer.descriptor();
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailerDescriptor.getSmtpServer());
        properties.put("mail.smtp.port", mailerDescriptor.getSmtpPort());
        properties.put("mail.smtp.auth", mailerDescriptor.getSmtpAuthUserName() != null);
        properties.put("mail.smtp.starttls.enable", tlsEnable);

        this.session = Session.getDefaultInstance(properties, mailerDescriptor.getSmtpAuthUserName() != null ?
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(mailerDescriptor.getSmtpAuthUserName(), mailerDescriptor.getSmtpAuthPassword());
                    }
                } : null);
    }

    public void sendMail(Email email, Set<Address> defaultRecipients) throws MessagingException, IOException {
        MessageBuilder messageBuilder = new MessageBuilder(email, defaultRecipients);
        if (messageBuilder.isMsgHasRecipients()) {
            Transport.send(messageBuilder.buildMessage(session));
        } else {
            LOGGER.log(Level.INFO, "No recipients to send mail.");
        }
    }

}