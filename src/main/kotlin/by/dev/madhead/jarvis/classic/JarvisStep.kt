package by.dev.madhead.jarvis.classic

import by.dev.madhead.jarvis.Jarvis
import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.creator.EmailCreator
import by.dev.madhead.jarvis.util.createDefaultAddressesSet
import by.dev.madhead.jarvis.util.isValidAddresses
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

class JarvisStep
@DataBoundConstructor
constructor(val defaultRecipients: String?) : Notifier() {

    override fun perform(build: AbstractBuild<*, *>, launcher: Launcher, listener: BuildListener): Boolean {
        try {
            Jarvis().sendMail(
                    email = EmailCreator(build, listener).create(),
                    defaultRecipientsAddresses = createDefaultAddressesSet(build, defaultRecipients))
            return true
        } catch (exception: Throwable) {
            throw AbortException(exception.message)
        }
    }

    override fun getRequiredMonitorService() = BuildStepMonitor.BUILD

    @Extension
    class JarvisStepDescriptor : BuildStepDescriptor<Publisher>() {
        override fun getDisplayName(): String = Messages.jarvis_displayName()

        override fun isApplicable(jobType: Class<out AbstractProject<*, *>>) = true

        fun doCheckDefaultRecipients(@QueryParameter defaultRecipients: String?) =
                if (isValidAddresses(defaultRecipients)) FormValidation.ok()
                else FormValidation.error(Messages.jarvis_validation_email())
    }

}
