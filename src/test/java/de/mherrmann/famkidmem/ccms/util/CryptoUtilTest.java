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

}
