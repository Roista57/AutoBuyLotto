package com.backend.member.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESUtil {
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int AES_KEY_BIT = 256;

    private SecretKey key;

    // application.properties에서 키 값을 읽어옴
    @Value("${aes.secret.key}")
    private String secretKeyBase64;

    @PostConstruct
    public void init() throws Exception {
        // Base64로 인코딩된 키 값을 디코딩하여 SecretKey 생성
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyBase64);
        this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    // 암호화
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);

        // IV를 생성
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        // IV와 암호화된 데이터를 함께 Base64로 인코딩하여 반환
        byte[] encryptedDataWithIv = new byte[IV_LENGTH_BYTE + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, IV_LENGTH_BYTE);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, IV_LENGTH_BYTE, encryptedData.length);

        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    // 복호화
    public String decrypt(String encryptedData) throws Exception {
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);

        // IV를 추출
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decodedData, 0, iv, 0, IV_LENGTH_BYTE);

        // 암호화된 데이터를 추출
        byte[] encryptedDataBytes = new byte[decodedData.length - IV_LENGTH_BYTE];
        System.arraycopy(decodedData, IV_LENGTH_BYTE, encryptedDataBytes, 0, encryptedDataBytes.length);

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decryptedData = cipher.doFinal(encryptedDataBytes);

        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
