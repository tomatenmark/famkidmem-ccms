package de.mherrmann.famkidmem.ccms.utils;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoUtil {


    public String generateSecureRandomCredential() {
        byte[] bytes = generateSecureRandomBytes(20);
        return toBase64(bytes);
    }

    public byte[] generateSecureRandomKeyParam(){
        return generateSecureRandomBytes(16);
    }

    public String toBase64(byte[] bytes){
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }

    public byte[] encrypt(byte[] plain, byte[] key, byte[] iv) throws GeneralSecurityException {
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(plain);
    }

    public byte[] encryptSingleBlock(byte[] plain, byte[] key) throws GeneralSecurityException {
        if(plain.length != 16){
            throw new GeneralSecurityException("Single block encryption: Byte count must be exact 16");
        }
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NOPADDING");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(plain);
    }

    //https://stackoverflow.com/questions/46261055/how-to-generate-a-securerandom-string-of-length-n-in-java
    private byte[] generateSecureRandomBytes(int length){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }
}
