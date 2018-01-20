package by.dev.madhead.jarvis.util;

import hudson.model.Run;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashSet;
import java.util.Set;

public class RecipientParser {

    private final static Logger LOGGER = LogManager.getLogger();

    private static final String RECIPIENTS_SPLIT_PATTERN = "[;, ]";

    public static Set<Address> createDefaultAddressesSet(Run<?, ?> run, String recipients) {
        Set<Address> defaultAddresses = new HashSet<>();

        String builderAddress = AddressSearcher.findBuilderAddress(run);
        if (builderAddress != null) {
            addStringAsAddress(defaultAddresses, builderAddress);
        }

        if (recipients != null) {
            for (String recipientAddress : recipients.split(RECIPIENTS_SPLIT_PATTERN)) {
                addStringAsAddress(defaultAddresses, recipientAddress);
            }
        }

        return defaultAddresses;
    }

    public static boolean isValidAddresses(String recipients) {
        if (recipients != null) {
            for (String recipient : recipients.split(RECIPIENTS_SPLIT_PATTERN)) {
                if (!isValidAddress(recipient)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isValidAddress(String address) {
        return address.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]+$");
    }

    public static void addStringAsAddress(Set<Address> addresses, String address) {
        if (isValidAddress(address)) {
            try {
                addresses.add(new InternetAddress(address));
            } catch (AddressException e) {
                LOGGER.log(Level.WARN, "Error while parsing email address: [\"" + address + "\"].", e);
            }
        } else {
            LOGGER.log(Level.WARN, "Not valid email address: [\"" + address + "\"].");
        }
    }

}