package by.dev.madhead.jarvis.util;

import hudson.model.Cause.UserIdCause;
import hudson.model.Run;
import hudson.model.User;
import hudson.tasks.Mailer;

public class AddressSearcher {

    private AddressSearcher() {
    }

    public static String findById(String userId) {
        User user = User.getOrCreateByIdOrFullName(userId);
        return user.getProperty(Mailer.UserProperty.class).getAddress();
    }

    public static String findBuilderAddress(Run<?, ?> run) {
        UserIdCause userIdCause = run.getCause(UserIdCause.class);
        return userIdCause != null ? findById(userIdCause.getUserId()) : null;
    }

}