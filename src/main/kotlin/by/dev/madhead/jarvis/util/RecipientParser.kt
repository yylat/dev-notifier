package by.dev.madhead.jarvis.util

import hudson.model.Run
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import javax.mail.Address
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

typealias AddressSet = Set<Address>

class RecipientParser {

    private val logger = LogManager.getLogger()

    private val recipientSplitPattern = "[;, ]"

    fun createDefaultAddressesSet(run: Run<*, *>, recipients: String?): AddressSet {
        val defaultAddresses = mutableSetOf<Address>()

        AddressSearcher().findBuilderAddress(run)?.let { addStringAsAddress(defaultAddresses, it) }

        recipients?.let {
            recipients.split(recipientSplitPattern).forEach { addStringAsAddress(defaultAddresses, it) }
        }

        return defaultAddresses
    }

    fun isValidAddress(address: String): Boolean {
        return address.matches(Regex("([a-zA-Z0-9]+[-_.]?)+@[a-z0-9]+\\.[a-z]+$"))
    }

    fun isValidAddresses(recipients: String?): Boolean {
        recipients?.let {
            recipients.split(recipientSplitPattern).forEach {
                if (!isValidAddress(it)) return false
            }
        }
        return true
    }

    fun addStringAsAddress(addresses: MutableSet<Address>, address: String) {
        if (isValidAddress(address)) {
            try {
                addresses.add(InternetAddress(address))
            } catch (e: AddressException) {
                logger.log(Level.WARN, "Error while parsing email address: [$address].", e)
            }
        }
    }

}
