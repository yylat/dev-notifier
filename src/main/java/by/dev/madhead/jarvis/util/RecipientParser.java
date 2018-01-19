package by.dev.madhead.jarvis.util;

import hudson.model.Run;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipientParser {

    private final static Logger LOGGER = Logger.getLogger(RecipientParser.class.getName());

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
        for (String recipient : recipients.split(RECIPIENTS_SPLIT_PATTERN)) {
            if (!isValidAddress(recipient)) {
                return false;
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
                LOGGER.log(Level.WARNING, "Error while parsing email address: [\"" + address + "\"].", e);
            }
        } else {
            LOGGER.log(Level.WARNING, "Not valid email address: [\"" + address + "\"].");
        }
    }

}