package by.dev.madhead.jarvis.util;

import hudson.model.Run;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(AddressSearcher.class)
public class RecipientParserTest {

    private Run<?, ?> run = mock(Run.class);

    private String builderAddress;
    private boolean validAddress;
    private String defaultRecipients;
    private boolean validAddresses;
    private int expectedSize;

    public RecipientParserTest(String builderAddress, boolean validAddress, String defaultRecipients, boolean validAddresses, int expectedSize) {
        this.builderAddress = builderAddress;
        this.validAddress = validAddress;
        this.defaultRecipients = defaultRecipients;
        this.validAddresses = validAddresses;
        this.expectedSize = expectedSize;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"address@email.com", true, "first@email.com,second@email.com,third@email.com", true, 4},
                {"address@email.org", true, "first@email.com;second@email.com;third@email.com", true, 4},
                {"address@email.by", true, "first@email.com second@email.com third@email.com", true, 4},
                {"address", false, "first@email.com secondemail.com third@email.com", false, 2},
                {"@gmail", false, "first@email.com second@email.com third@.com", false, 2},
                {"", false, "first second@email.com third@email.com", false, 2}
        });
    }

    @Test
    public void createDefaultAddressesSet() {
        mockStatic(AddressSearcher.class);
        when(AddressSearcher.findBuilderAddress(Mockito.any())).thenReturn(builderAddress);

        assertThat(RecipientParser.createDefaultAddressesSet(run, defaultRecipients),
                hasSize(expectedSize));
    }

    @Test
    public void isValidAddress() {
        assertThat(RecipientParser.isValidAddress(builderAddress), is(validAddress));
    }

    @Test
    public void isValidAddresses() {
        assertThat(RecipientParser.isValidAddresses(defaultRecipients), is(validAddresses));
    }

    @Test
    public void addStringAsAddress() throws AddressException {
        Set<Address> addresses = mock(Set.class);
        RecipientParser.addStringAsAddress(addresses, builderAddress);
        if (validAddress) {
            verify(addresses, times(1)).add(new InternetAddress(builderAddress));
        }
    }

}