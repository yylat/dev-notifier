package by.dev.madhead.jarvis.util

import hudson.model.Run
import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito.mock
import javax.mail.Address

@RunWith(Parameterized::class)
class RecipientParserTest(
        private val builderAddress: String,
        private val isValidBuilderAddress: Boolean,
        private val defaultRecipients: String,
        private val isValidDefaultRecipientsAddresses: Boolean) {

    private val run = mock(Run::class.java)

    private val recipientParser = RecipientParser()

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                arrayOf("address@email.com", true, "first@email.com,second@email.com,third@email.com", true),
                arrayOf("address@email.org", true, "first@email.com;second@email.com;third@email.com", true),
                arrayOf("address@email.by", true, "first@email.com second@email.com third@email.com", true),
                arrayOf("address", false, "first@email.com secondemail.com third@email.com", false),
                arrayOf("@gmail", false, "first@email.com second@email.com third@.com", false),
                arrayOf("", false, "first second@email.com third@email.com", false)
        )
    }

    @Test
    fun isValidAddress() {
        assertThat(recipientParser.isValidAddress(builderAddress), `is`(isValidBuilderAddress))
    }

    @Test
    fun isValidAddresses() {
        assertThat(recipientParser.isValidAddresses(defaultRecipients), `is`(isValidDefaultRecipientsAddresses))
    }

    @Test
    fun addStringAsAddress() {
        val addressesSet = mutableSetOf<Address>()
        recipientParser.addStringAsAddress(addressesSet, builderAddress)
        if (isValidBuilderAddress) assertThat(addressesSet, hasSize(1))
        else assertThat(addressesSet, empty())
    }

}