package by.dev.madhead.devnotifier.util

import hudson.model.Run
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import javax.mail.Address
import javax.mail.internet.AddressException
import javax.mail.internet.InternetAddress

private val addressPattern = Regex("([a-zA-Z0-9]+[-_.]?)+@[a-z0-9]+\\.[a-z]+$")
private val recipientSplitPattern = Regex("[;, ]")

fun createDefaultAddressesSet(run: Run<*, *>, recipients: String?): Set<Address> {

    val arrayOfAddresses = recipients?.split(recipientSplitPattern)
            ?.mapNotNull { stringToAddress(it) }
            ?.toTypedArray() ?: emptyArray()

    return findJenkinsBuilderAddress(run)
            ?.let { stringToAddress(it) }
            ?.let { setOf(it, *arrayOfAddresses) } ?: setOf(*arrayOfAddresses)
}

fun isValidAddress(address: String) = address.matches(addressPattern)

fun isValidAddresses(recipients: String?): Boolean {
    if (recipients != null && recipients.isNotBlank()) {
        recipients.split(recipientSplitPattern).forEach {
            if (!isValidAddress(it)) return false
        }
    }
    return true
}

fun stringToAddress(address: String): InternetAddress? {
    if (isValidAddress(address)) {
        return InternetAddress(address)
    } else {
        return null
    }
}

fun addStringAsAddress(addresses: MutableSet<Address>, address: String) {
    if (isValidAddress(address)) {
        try {
            addresses.add(InternetAddress(address))
        } catch (e: AddressException) {
            LogManager.getLogger().log(Level.WARN, "Error while parsing email address: [$address].", e)
        }
    }
}
