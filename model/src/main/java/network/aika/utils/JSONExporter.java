package network.aika.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import network.aika.fielddefs.Type;

import java.io.File;
import java.io.IOException;

public class JSONExporter {

    public static String export(Type type) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        ObjectWriter objectWriter = objectMapper.writerFor(Type.class);

        try {
            return objectWriter.writeValueAsString(type);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse " + Type.class.getSimpleName() + " JSON String.", e);
        }
    }
}
