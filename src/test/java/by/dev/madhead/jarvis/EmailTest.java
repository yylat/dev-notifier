package by.dev.madhead.jarvis;

import by.dev.madhead.jarvis.model.*;
import hudson.tasks.Mailer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Random;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Mailer.class)
@PowerMockIgnore({"com.sun.mail.*", "javax.mail.*", "javax.activation.*"})
public class EmailTest {

    private static Jarvis jarvis;

    private final String to = System.getenv("JARVIS_TO");

    @BeforeClass
    public static void setUp() {
        mockStatic(Mailer.class);

        Mailer.DescriptorImpl mailerDescriptor = mock(Mailer.DescriptorImpl.class);
        when(Mailer.descriptor()).thenReturn(mailerDescriptor);

        when(mailerDescriptor.getSmtpServer()).thenReturn(System.getenv("JARVIS_SMTP_HOST"));
        when(mailerDescriptor.getSmtpPort()).thenReturn(System.getenv("JARVIS_SMTP_PORT"));
        when(mailerDescriptor.getSmtpAuthUserName()).thenReturn(System.getenv("JARVIS_SMTP_USER"));
        when(mailerDescriptor.getSmtpAuthPassword()).thenReturn(System.getenv("JARVIS_SMTP_PASSWORD"));

        jarvis = new Jarvis(Boolean.valueOf(System.getenv("JARVIS_SMTP_TLS")));
    }

    @Test
    public void sendNoOne() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.UNKNOWN,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<>()),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendPassed() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.PASSED,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<Change>() {{
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "mv -> cp",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                    add(new Change("df156a6",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "Fix #35: move document output settings upper in hierarchy of calls",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/df156a6"));
                                }}),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendFixed() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.FIXED,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<Change>() {{
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "Fix scripts",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                }}),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendBroken() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.BROKEN,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<Change>() {{
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "Fix scripts",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                }}),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendStillBroken() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.STILL_BROKEN,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<>()),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendFailed() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.FAILED,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<Change>() {{
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "mv -> cp",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "mv -> cp",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                }}),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendStillFailing() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.STILL_FAILING,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<>()),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    @Test
    public void sendUnknown() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.UNKNOWN,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<Change>() {{
                                    add(new Change(
                                            "217c8a5",
                                            new Author(
                                                    "madhead",
                                                    to),
                                            "mv -> cp",
                                            "https://github.com/jenkinsci/doktor-plugin/commit/217c8a5"));
                                }}),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    public void sendAborted() throws IOException, MessagingException {
        jarvis.sendMail(new Email(
                        new Repo(
                                "jenkinsci/doktor-plugin",
                                "https://github.com/jenkinsci/doktor-plugin"),
                        new Build(
                                random(1, 99),
                                "master",
                                "revision",
                                BuildStatus.ABORTED,
                                Duration.ofSeconds(random(30L, 86400)),
                                "https://travis-ci.org/jenkinsci/doktor-plugin/builds/291265649",
                                new ArrayList<>()
                        ),
                        new Extra(
                                "support@travis-ci.com")),
                null, null);
    }

    private int random(int start, int end) {
        return new Random().nextInt(end - start) + start;
    }

    private long random(long start, long end) {
        return start + (long) (Math.random() * (end - start));
    }

}