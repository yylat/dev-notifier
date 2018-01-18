package by.dev.madhead.jarvis;

import by.dev.madhead.jarvis.model.Change;
import by.dev.madhead.jarvis.model.Email;
import by.dev.madhead.jarvis.util.ContentMaker;
import hudson.tasks.Mailer;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Jarvis {

    private final Session session;

    public Jarvis(boolean tlsEnable) {
        Mailer.DescriptorImpl mailerDescriptor = Mailer.descriptor();
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailerDescriptor.getSmtpServer());
        properties.put("mail.smtp.port", mailerDescriptor.getSmtpPort());
        properties.put("mail.smtp.auth", mailerDescriptor.getSmtpAuthUserName() != null);
        properties.put("mail.smtp.starttls.enable", tlsEnable);

        this.session = Session.getDefaultInstance(properties, initAuthenticator(
                mailerDescriptor.getSmtpAuthUserName(),
                mailerDescriptor.getSmtpAuthPassword()));
    }

    public void sendMail(Email email, String builderAddress, String defaultRecipients) throws MessagingException, IOException {
        Address[] addresses = defineAddresses(builderAddress, defaultRecipients, email.getBuild().getChangeSet());
        if (addresses.length != 0) {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(Messages.jarvis_fromName()));
            msg.setRecipients(Message.RecipientType.TO, addresses);
            msg.setSubject(email.getSubject());
            msg.setContent(ContentMaker.buildContent(email));
            Transport.send(msg);
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

    private Address[] defineAddresses(String builderAddress, String defaultRecipients, List<Change> changes) throws AddressException {
        List<Address> addresses = new ArrayList<>();
        if (builderAddress != null) {
            addresses.add(new InternetAddress(builderAddress));
        }
        if (defaultRecipients != null) {
            for (String recipient : defaultRecipients.split("[;, ]")) {
                addresses.add(new InternetAddress(recipient));
            }
        }
        for (Change change : changes) {
            if (change.getAuthor().getEmail() != null) {
                addresses.add(new InternetAddress(change.getAuthor().getEmail()));
            }
        }
        return addresses.toArray(new Address[addresses.size()]);
    }

}