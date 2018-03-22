package by.dev.madhead.jarvis.util

import org.hamcrest.Matchers.*
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import javax.mail.Address

@RunWith(Parameterized::class)
class RecipientParserTest(
        private val builderAddress: String,
        private val isValidBuilderAddress: Boolean,
        private val defaultRecipients: String,
        private val isValidDefaultRecipientsAddresses: Boolean) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                arrayOf("address@email.com", true, "first@email.com,second@email.com,third@email.com", true),
                arrayOf("address@email.org", true, "first@email.com;second@email.com;third@email.com", true),
                arrayOf("address@email.by", true, "first@email.com second@email.com third@email.com", true),
                arrayOf("address", false, "first@email.com secondemail.com third@email.com", false),
                arrayOf("@gmail", false, "first@email.com second@email.com third@.com", false),
                arrayOf("email.com", false, "first second@email.com third@email.com", false),
                arrayOf("@", false, "", true),
                arrayOf("..", false, "    ", true)
        )
    }

    @Test
    fun isValidAddress() {
        assertThat(isValidAddress(builderAddress), `is`(isValidBuilderAddress))
    }

    @Test
    fun isValidAddresses() {
        assertThat(isValidAddresses(defaultRecipients), `is`(isValidDefaultRecipientsAddresses))
    }

    @Test
    fun addStringAsAddress() {
        val addressesSet = mutableSetOf<Address>()
        addStringAsAddress(addressesSet, builderAddress)
        if (isValidBuilderAddress) assertThat(addressesSet, hasSize(1))
        else assertThat(addressesSet, empty())
    }

}