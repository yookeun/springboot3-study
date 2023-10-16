package com.example.study.converter;

import com.querydsl.core.util.StringUtils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class YnToBooleanConverter implements AttributeConverter<String, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(String attribute) {
        if (StringUtils.isNullOrEmpty(attribute)) {
            return false;
        }
        return attribute.equals("Y");
    }

    @Override
    public String convertToEntityAttribute(Boolean dbData) {
        return dbData ? "Y" : "N";
    }
}
