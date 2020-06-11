package de.mherrmann.famkidmem.ccms.util;

import de.mherrmann.famkidmem.ccms.utils.CryptoUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CryptoUtilTest {

    @Autowired
    private CryptoUtil cryptoUtil;

    @Test
    public void shouldGetSecureRandomCredential(){
        String credential = cryptoUtil.generateSecureRandomCredential();

        assertThat(credential).matches("[a-zA-Z\\d\\-_]{27}");
    }

    @Test
    public void shouldGetSecureRandomKeyParam(){
        byte[] bytes = cryptoUtil.generateSecureRandomKeyParam();

        assertThat(bytes.length).isEqualTo(16);
    }

    @Test
    public void shouldEncryptECB() throws Exception {
        String keyHex = "000102030405060708090A0B0C0D0E0F";
        String plainHex = "00112233445566778899AABBCCDDEEFF";
        String expectedEncryptedHex = "69C4E0D86A7B0430D8CDB78070B4C55A";

        byte[] encrypted = cryptoUtil.encryptSingleBlock(hexStringToByteArray(plainHex), hexStringToByteArray(keyHex));

        assertThat(encrypted).isEqualTo(hexStringToByteArray(expectedEncryptedHex));
    }

    @Test
    public void shouldEncryptCBCSingleBlock() throws Exception {
        String keyHex = "06a9214036b8a15b512e03d534120006";
        String ivHex = "3dafba429d9eb430b422da802c9fac41";
        String plainHex = "53696E676C6520626C6F636B206D7367";
        String expectedEncryptedHex = "e353779c1079aeb82708942dbe77181ab97c825e1c785146542d396941bce55d";

        byte[] encrypted = cryptoUtil.encrypt(hexStringToByteArray(plainHex), hexStringToByteArray(keyHex), hexStringToByteArray(ivHex));

        assertThat(encrypted).isEqualTo(hexStringToByteArray(expectedEncryptedHex));
    }

    @Test
    public void shouldEncryptCBCTwoBlocks() throws Exception {
        String keyHex = "c286696d887c9aa0611bbb3e2025a45a";
        String ivHex = "562e17996d093d28ddb3ba695a2e6f58";
        String plainHex = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f";
        String expectedEncryptedHex = "d296cd94c2cccf8a3a863028b5e1dc0a7586602d253cfff91b8266bea6d61ab1bcfd81022202366bde6dd260a15841a1";

        byte[] encrypted = cryptoUtil.encrypt(hexStringToByteArray(plainHex), hexStringToByteArray(keyHex), hexStringToByteArray(ivHex));

        assertThat(encrypted).isEqualTo(hexStringToByteArray(expectedEncryptedHex));
    }



    public static void main(String[] args) {
        byte[] test = new byte[]{-46, -106, -51, -108, -62, -52, -49, -118, 58, -122, 48, 40, -75, -31, -36, 10, 117, -122, 96, 45, 37, 60, -1, -7, 27, -126, 102, -66, -90, -42, 26, -79, -68, -3, -127, 2, 34, 2, 54, 107, -34, 109, -46, 96, -95, 88, 65, -95};
        for(byte b : test){
            System.out.print(byteToHex(b));
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}
