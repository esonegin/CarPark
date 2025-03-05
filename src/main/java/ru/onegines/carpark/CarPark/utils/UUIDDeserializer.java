package ru.onegines.carpark.CarPark.utils;

/**
 * @author onegines
 * @date 02.03.2025
 */

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.UUID;

public class UUIDDeserializer extends JsonDeserializer<UUID> {
    @Override
    public UUID deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String uuidStr = p.getText();
        if (uuidStr.equalsIgnoreCase("GENERATED_GUID")) {
            return UUID.randomUUID(); // Генерируем новый UUID, если пришел некорректный
        }
        return UUID.fromString(uuidStr);
    }
}
