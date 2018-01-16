package by.dev.madhead.jarvis.util;

import hudson.model.Cause.UserIdCause;
import hudson.model.Run;
import hudson.model.User;
import hudson.tasks.Mailer;

public final class AddressExtractor {

    private AddressExtractor() {
    }

    public static String extract(String userId) {
        User user = User.getOrCreateByIdOrFullName(userId);
        return user.getProperty(Mailer.UserProperty.class).getAddress();
    }

    public static String extractBuilderAddress(Run<?, ?> run) {
        UserIdCause userIdCause = run.getCause(UserIdCause.class);
        return userIdCause != null ? extract(userIdCause.getUserId()) : null;
    }

}