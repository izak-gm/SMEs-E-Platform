package com.izak.payment_service.OrderEvent.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Map;

@Converter
public class JsonMapConverter
      implements AttributeConverter<Map<String, Object>, String> {

  private static final ObjectMapper mapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(Map<String, Object> attribute) {
    try {
      return mapper.writeValueAsString(attribute);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to serialize metadata");
    }
  }

  @Override
  public Map<String, Object> convertToEntityAttribute(String dbData) {
    try {
      return mapper.readValue(dbData, Map.class);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to deserialize metadata");
    }
  }
}

