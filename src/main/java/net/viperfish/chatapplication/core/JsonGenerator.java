package net.viperfish.chatapplication.core;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.StringWriter;

public final class JsonGenerator {

    protected ObjectMapper objectMapper;

    public JsonGenerator() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

    }

    public String toJson(Object o) throws JsonGenerationException, JsonMappingException {
        StringWriter w = new StringWriter();
        try {
            objectMapper.writeValue(w, o);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return w.toString();

    }

    public <T> T fromJson(Class<T> srcClass, String data) throws JsonParseException, JsonMappingException {
        T result = null;
        try {
            result = objectMapper.readValue(data, srcClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
