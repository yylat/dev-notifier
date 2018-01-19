package by.dev.madhead.jarvis;

import by.dev.madhead.jarvis.model.Email;
import by.dev.madhead.jarvis.util.MessageBuilder;
import hudson.tasks.Mailer;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Jarvis {

    private static final Logger LOGGER = Logger.getLogger(Jarvis.class.getName());

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
            LOGGER.log(Level.FINE, "No recipients to send mail.");
        }
    }

    private Authenticator initAuthenticator(String smtpAuthUserName, String smtpAuthPassword) {
        return smtpAuthUserName != null ?
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(smtpAuthUserName, smtpAuthPassword);
                    }
                } : null;
    }

}