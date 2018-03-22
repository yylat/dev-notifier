package by.dev.madhead.jarvis.classic

import by.dev.madhead.jarvis.Jarvis
import by.dev.madhead.jarvis.Messages
import by.dev.madhead.jarvis.creator.EmailCreatorFactory
import by.dev.madhead.jarvis.util.RecipientParser
import hudson.Extension
import hudson.Launcher
import hudson.model.AbstractBuild
import hudson.model.AbstractProject
import hudson.model.BuildListener
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Notifier
import hudson.tasks.Publisher
import hudson.util.FormValidation
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.QueryParameter

class JarvisStep
@DataBoundConstructor
constructor(val defaultRecipients: String?) : Notifier() {

    override fun perform(build: AbstractBuild<*, *>, launcher: Launcher, listener: BuildListener): Boolean {
        Jarvis().sendMail(
                email = EmailCreatorFactory().getCreator(build, listener).create(),
                defaultRecipientsAddresses = RecipientParser().createDefaultAddressesSet(build, defaultRecipients))
        return true
    }

    @Extension
    class JarvisStepDescriptor : BuildStepDescriptor<Publisher>() {
        override fun getDisplayName(): String = Messages.jarvis_displayName()

        override fun isApplicable(jobType: Class<out AbstractProject<*, *>>) = true

        fun doCheckDefaultRecipients(@QueryParameter defaultRecipients: String?): FormValidation {
            return if (RecipientParser().isValidAddresses(defaultRecipients)) FormValidation.ok()
            else FormValidation.error(Messages.jarvis_validation_email())
        }
    }

}
