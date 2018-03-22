package by.dev.madhead.jarvis.util

import hudson.model.Cause
import hudson.model.Run
import hudson.model.User
import hudson.tasks.Mailer

fun findJenkinsUserAddressById(userId: String?) =
        if (userId != null)
            User.getOrCreateByIdOrFullName(userId)
                    .getProperty(Mailer.UserProperty::class.java)
                    .address
        else null

fun findJenkinsBuilderAddress(run: Run<*, *>) =
        findJenkinsUserAddressById(run.getCause(Cause.UserIdCause::class.java)?.userId)

