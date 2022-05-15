package com.store.app.departmentactivitymonitor.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.time.Instant;

public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    private final StringToInstantParser parser;

    protected InstantJsonDeserializer() {
        this.parser = new StringToInstantParser();
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try {
            return this.parser.parse(p.getText());
        } catch (ParseException e) {
            throw new JsonParseException(p, e.getMessage());
        }
    }
}
