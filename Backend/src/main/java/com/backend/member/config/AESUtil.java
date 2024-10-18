package com.backend.member.config;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;
    
    private SecretKey key;
    private byte[] iv;

    public AESUtil() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_BIT, SecureRandom.getInstanceStrong());
        this.key = keyGen.generateKey();
        this.iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
    }

    // 암호화
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    // 복호화
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(decodedData);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
