package com.example.study.common;

import com.querydsl.core.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class EncDecConverter implements AttributeConverter<String, String> {

    private final EncryptHandler encryptHandler;

    public EncDecConverter(EncryptHandler encryptHandler) {
        this.encryptHandler = encryptHandler;
    }

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (StringUtils.isNullOrEmpty(plainText)) {
            return null;
        }
        String encrypted;
        try {
            encrypted = encryptHandler.encrypt(plainText);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
        return encrypted;
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        if (StringUtils.isNullOrEmpty(encrypted)) {
            return null;
        }
        String decrypted;
        try {
            decrypted = encryptHandler.decrypt(encrypted);
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
        return decrypted;
    }
}
