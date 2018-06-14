package by.dev.madhead.devnotifier.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import javax.mail.Address

@RunWith(Parameterized::class)
class RecipientParserTest {

    @ParameterizedTest
    @MethodSource("builderAddressSource")
    fun isValidAddress(builderAddress: String, isValidBuilderAddress: Boolean) {
        Assertions.assertEquals(isValidBuilderAddress, by.dev.madhead.devnotifier.util.isValidAddress(builderAddress))
    }

    @ParameterizedTest
    @MethodSource("addressesSource")
    fun isValidAddresses(defaultRecipients: String, isValidDefaultRecipientsAddresses: Boolean) {
        Assertions.assertEquals(isValidDefaultRecipientsAddresses, by.dev.madhead.devnotifier.util.isValidAddresses(defaultRecipients))
    }

    @ParameterizedTest
    @MethodSource("builderAddressSource")
    fun addStringAsAddress(builderAddress: String, isValidBuilderAddress: Boolean) {
        val addressesSet = mutableSetOf<Address>()
        by.dev.madhead.devnotifier.util.addStringAsAddress(addressesSet, builderAddress)
        if (isValidBuilderAddress) Assertions.assertEquals(1, addressesSet.size)
        else Assertions.assertEquals(0, addressesSet.size)
    }

    companion object {
        @JvmStatic
        fun builderAddressSource() = listOf(
                arrayOf("address@email.com", true),
                arrayOf("address@email.by", true),
                arrayOf("address", false),
                arrayOf("@gmail", false),
                arrayOf("email.com", false),
                arrayOf("@", false)
        )

        @JvmStatic
        fun addressesSource() = listOf(
                arrayOf("first@email.com,second@email.com,third@email.com", true),
                arrayOf("first@email.com;second@email.com;third@email.com", true),
                arrayOf("first@email.com second@email.com third@email.com", true),
                arrayOf("first@email.com secondemail.com third@email.com", false),
                arrayOf("first@email.com second@email.com third@.com", false),
                arrayOf("first second@email.com third@email.com", false),
                arrayOf("", true),
                arrayOf("    ", true)
        )
    }

}