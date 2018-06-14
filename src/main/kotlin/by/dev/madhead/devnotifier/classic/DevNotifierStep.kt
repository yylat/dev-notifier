package by.dev.madhead.devnotifier.classic

import by.dev.madhead.devnotifier.Messages
import by.dev.madhead.devnotifier.creator.EmailCreator
import by.dev.madhead.devnotifier.util.createDefaultAddressesSet
import by.dev.madhead.devnotifier.util.isValidAddresses
import hudson.AbortException
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.BuildStepMonitor
import hudson.tasks.Notifier
import hudson.tasks.Publisher
import hudson.util.FormValidation
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class DevNotifierStep
@DataBoundConstructor
constructor(val defaultRecipients: String?) : Notifier() {

    override fun perform(build: AbstractBuild<*, *>, launcher: Launcher, listener: BuildListener): Boolean {
        try {
            by.dev.madhead.devnotifier.DevNotifier().sendMail(
                    email = EmailCreator(build, listener).create(),
                    defaultRecipientsAddresses = createDefaultAddressesSet(build, defaultRecipients),
                    logger = listener.logger)
            return true
        } catch (exception: Throwable) {
            throw AbortException(exception.message)
        }
    }

    override fun getRequiredMonitorService() = BuildStepMonitor.BUILD

    @Extension
    class DevNotifierStepDescriptor : BuildStepDescriptor<Publisher>() {
        override fun getDisplayName(): String = Messages.devnotifier_displayName()

        override fun isApplicable(jobType: Class<out AbstractProject<*, *>>) = true

        fun doCheckDefaultRecipients(@QueryParameter defaultRecipients: String?) =
                if (isValidAddresses(defaultRecipients)) FormValidation.ok()
                else FormValidation.error(Messages.devnotifier_validation_email())
    }

}
