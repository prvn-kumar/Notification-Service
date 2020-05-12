package com.payon.webhook.service;

import com.payon.webhook.model.WebHookNotification;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.Security;

// For Java and JVM-based languages, you might need to install unrestricted policy file for JVM,
// which is provided by Sun. Please refer BouncyCastle FAQ if you get
// java.lang.SecurityException: Unsupported keysize or algorithm parameters or
// java.security.InvalidKeyException: Illegal key size.

// If you cannot install unrestricted policy file for JVM because of some reason, you can try with reflection: See here.

public class DecryptService {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String decrypt(WebHookNotification notification, String keyFromConfiguration) throws Exception {
        // Convert data to process
        byte[] key = DatatypeConverter.parseHexBinary(keyFromConfiguration);
        byte[] iv = DatatypeConverter.parseHexBinary(notification.getIvHeader());
        byte[] authTag = DatatypeConverter.parseHexBinary(notification.getAuthHeader());
        byte[] encryptedText = DatatypeConverter.parseHexBinary(notification.getEncryptedBody());

        // Unlike other programming language, We have to append auth tag at the end of encrypted text in Java
        byte[] cipherText = ArrayUtils.addAll(encryptedText, authTag);

        // Prepare decryption
        SecretKeySpec keySpec = new SecretKeySpec(key, 0, 32, "AES");
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));

        // Decrypt
        byte[] bytes = cipher.doFinal(cipherText);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
