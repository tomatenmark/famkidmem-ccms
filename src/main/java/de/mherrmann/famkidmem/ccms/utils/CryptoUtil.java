package de.mherrmann.famkidmem.ccms.utils;

import de.mherrmann.famkidmem.ccms.Application;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

@Service
public class CryptoUtil {


    public String generateSecureRandomCredential() {
        byte[] bytes = generateSecureRandomBytes(20);
        return toUrlBase64(bytes);
    }

    public String generateSecureRandomKeyParamHex() {
        byte[] bytes = generateSecureRandomBytes(16);
        return toHex(bytes);
    }

    public byte[] generateSecureRandomKeyParam(){
        return generateSecureRandomBytes(16);
    }

    public String encryptKey(byte[] key) throws GeneralSecurityException {
        byte[] masterKey = fromBase64(Application.getSettings().getMasterKey());
        return toBase64(encryptSingleBlock(key, masterKey));
    }

    public String toBase64(byte[] bytes){
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(bytes);
    }

    public String toUrlBase64(byte[] bytes){
        String base64 = toBase64(bytes);
        return base64.replace("+", "-").replace("/", "_");
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

    private byte[] fromBase64(String ascii){
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(ascii);
    }

    private byte[] generateSecureRandomBytes(int length){
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        return bytes;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte b : bytes){
            char[] hexDigits = new char[2];
            hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((b & 0xF), 16);
            sb.append(new String(hexDigits));
        }
        return sb.toString();
    }
}
