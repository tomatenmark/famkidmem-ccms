package de.mherrmann.famkidmem.ccms.utils;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class CryptoUtil {

    //https://stackoverflow.com/questions/46261055/how-to-generate-a-securerandom-string-of-length-n-in-java
    public String generateSecureRandomCredential() {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[20];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

}
