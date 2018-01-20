package by.dev.madhead.jarvis.util;

import hudson.model.Cause.UserIdCause;
import hudson.model.Run;
import hudson.model.User;
import hudson.tasks.Mailer;

public class AddressSearcher {

    private AddressSearcher() {
    }

    /**
     * Finds email address of user with user id in Jenkins.
     *
     * @param userId user id in Jenkins
     * @return email address of user
     */
    public static String findById(String userId) {
        User user = User.getOrCreateByIdOrFullName(userId);
        return user.getProperty(Mailer.UserProperty.class).getAddress();
    }

    /**
     * Finds email address of user, who started run.
     *
     * @param run run
     * @return email address of user
     */
    public static String findBuilderAddress(Run<?, ?> run) {
        UserIdCause userIdCause = run.getCause(UserIdCause.class);
        return userIdCause != null ? findById(userIdCause.getUserId()) : null;
    }

}