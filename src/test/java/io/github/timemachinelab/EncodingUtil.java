package io.github.timemachinelab;

import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EncodingUtil {

    @Autowired
    StringEncryptor encryptor;

    @Test
    public void getPass() {
        // String url = encryptor.encrypt("jdbc:mysql://117.72.211.46:3306/config_meow?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
        String username = encryptor.encrypt("117.72.211.46");
        // String password = encryptor.encrypt("Welsir123456@");


        // System.out.println("url Encrypted: " + url);
        System.out.println("username Encrypted: " + username);
        // System.out.println("password Encrypted: " + password);

    }
}
