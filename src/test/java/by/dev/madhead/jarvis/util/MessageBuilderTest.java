package by.dev.madhead.jarvis.util;

import by.dev.madhead.jarvis.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.core.Is.is;

@RunWith(Parameterized.class)
public class MessageBuilderTest {

    private MessageBuilder messageBuilder;
    private boolean expectedMsgHasRecipients;
    private int expectedRecipientsSize;

    public MessageBuilderTest(MessageBuilder messageBuilder, boolean expectedMsgHasRecipients, int expectedRecipientsSize) {
        this.messageBuilder = messageBuilder;
        this.expectedMsgHasRecipients = expectedMsgHasRecipients;
        this.expectedRecipientsSize = expectedRecipientsSize;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws AddressException {
        return Arrays.asList(new Object[][]{
                {new MessageBuilder(
                        new Email(
                                new Repo("slug", "link"),
                                new Build(1, "branch", "revision",
                                        BuildStatus.PASSED, Duration.ofSeconds(100), "link",
                                        new ArrayList<Change>() {{
                                            add(new Change(
                                                    "revision1",
                                                    new Author(
                                                            "username1",
                                                            "username1@email.com"),
                                                    "message",
                                                    "link"));
                                            add(new Change("revision2",
                                                    new Author(
                                                            "username2",
                                                            "username2@email.com"),
                                                    "message",
                                                    "link"));
                                        }}),
                                new Extra("supportEmail")),
                        new HashSet<Address>() {{
                            add(new InternetAddress("address@email.com"));
                        }}),
                        true, 3},

                {new MessageBuilder(
                        new Email(
                                new Repo("slug", "link"),
                                new Build(1, "branch", "revision",
                                        BuildStatus.PASSED, Duration.ofSeconds(100), "link",
                                        new ArrayList<>()),
                                new Extra("supportEmail")),
                        new HashSet<>()),
                        false, 0},

                {new MessageBuilder(
                        new Email(
                                new Repo("slug", "link"),
                                new Build(1, "branch", "revision",
                                        BuildStatus.PASSED, Duration.ofSeconds(100), "link",
                                        new ArrayList<>()),
                                new Extra("supportEmail")),
                        new HashSet<Address>() {{
                            add(new InternetAddress("address@email.com"));
                        }}),
                        true, 1},
                
                {new MessageBuilder(
                        new Email(
                                new Repo("slug", "link"),
                                new Build(1, "branch", "revision",
                                        BuildStatus.PASSED, Duration.ofSeconds(100), "link",
                                        new ArrayList<Change>() {{
                                            add(new Change(
                                                    "revision1",
                                                    new Author(
                                                            "username1",
                                                            "username1@email.com"),
                                                    "message",
                                                    "link"));
                                            add(new Change("revision2",
                                                    new Author(
                                                            "username2",
                                                            "username2@email.com"),
                                                    "message",
                                                    "link"));
                                        }}),
                                new Extra("supportEmail")),
                        new HashSet<>()),
                        true, 2}
        });
    }

    @Test
    public void isMsgHasRecipients() {
        assertThat(messageBuilder.isMsgHasRecipients(), is(expectedMsgHasRecipients));
    }

    @Test
    public void buildMessage() throws IOException, MessagingException {
        if (expectedMsgHasRecipients) {
            assertThat(messageBuilder.buildMessage(Mockito.any()).getAllRecipients(),
                    arrayWithSize(expectedRecipientsSize));
        } else {
            assertThat(messageBuilder.buildMessage(Mockito.any()).getAllRecipients(),
                    nullValue());
        }
    }

}