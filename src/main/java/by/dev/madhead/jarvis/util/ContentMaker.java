package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.model.BuildStatus;
import by.dev.madhead.jarvis.model.Email;
import org.apache.commons.io.Charsets;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.Random;

public class ContentMaker {

    private final static String PATH_TO_IMAGES = "/by/dev/madhead/jarvis/images/";
    private final static String IMAGE_PNG = "image/png";

    private ContentMaker() {
    }

    public static MimeMultipart buildContent(Email email) throws IOException, MessagingException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(Charsets.UTF_8.name());
        templateResolver.setPrefix("/by/dev/madhead/jarvis/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(true);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("randomSuccessExhortation", new Random().nextInt(4));
        context.setVariable("randomFailureExhortation", new Random().nextInt(2));

        MimeBodyPart header = new MimeBodyPart();
        header.setContent(templateEngine.process("jarvis", context), "text/html; charset=utf-8");

        MimeMultipart content = new MimeMultipart();
        content.addBodyPart(header);

        content.addBodyPart(buildBodyPart(
                "<status.png>", "Build status",
                PATH_TO_IMAGES + "status-"
                        + image(email.getBuild().getStatus()) + ".png", IMAGE_PNG));

        content.addBodyPart(buildBodyPart(
                "<duration.png>", "Duration",
                PATH_TO_IMAGES + "duration-"
                        + image(email.getBuild().getStatus()) + ".png", IMAGE_PNG));

        content.addBodyPart(buildBodyPart(
                "<jenkins.png>", "Jenkins",
                PATH_TO_IMAGES + "jenkins"
                        + (email.getBuild().getStatus() == BuildStatus.PASSED
                        || email.getBuild().getStatus() == BuildStatus.FIXED
                        ? "" : "-in-fire") + ".png", IMAGE_PNG));
        return content;
    }

    private static MimeBodyPart buildBodyPart(String contentID, String description, String resourceName, String resourceType) throws MessagingException, IOException {
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContentID(contentID);
        bodyPart.setDescription(description);
        bodyPart.setDisposition("inline; filename=\"" + bodyPart.getContentID().replaceAll("[<>]", "") + "\"");
        bodyPart.setDataHandler(new DataHandler(
                new ByteArrayDataSource(ContentMaker.class.getResourceAsStream(resourceName), resourceType)));
        return bodyPart;
    }

    private static String image(BuildStatus buildStatus) {
        if (BuildStatus.PASSED.equals(buildStatus) || BuildStatus.FIXED.equals(buildStatus)) {
            return "success";
        }
        if (BuildStatus.BROKEN.equals(buildStatus) || BuildStatus.FAILED.equals(buildStatus) || BuildStatus.STILL_FAILING.equals(buildStatus)) {
            return "failure";
        }
        return "unknown";
    }

}