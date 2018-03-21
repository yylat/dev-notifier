package by.dev.madhead.jarvis.util

import hudson.model.Cause
import hudson.model.Run
import hudson.model.User
import hudson.tasks.Mailer

class AddressSearcher {

    fun findById(userId: String?): String? {
        return if (userId != null)
            User.getOrCreateByIdOrFullName(userId)
                    .getProperty(Mailer.UserProperty::class.java)
                    .address
        else null
    }

    fun findBuilderAddress(run: Run<*, *>): String? {
        return findById(run.getCause(Cause.UserIdCause::class.java)?.userId)
    }

}