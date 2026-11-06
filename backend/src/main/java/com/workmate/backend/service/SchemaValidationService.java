package com.workmate.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SchemaValidationService {

    private final ObjectMapper objectMapper;
    private final JsonSchema schema;

    public SchemaValidationService(ObjectMapper objectMapper) throws IOException {
        this.objectMapper = objectMapper;

        JsonNode schemaNode = objectMapper.readTree(
                new ClassPathResource("schemas/intent-classification-schema.json").getInputStream());

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(
                SpecVersion.VersionFlag.V202012);

        this.schema = factory.getSchema(schemaNode);
    }

    public void validate(String json) {
        final JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Claude returned malformed JSON", exception);
        }

        Set<ValidationMessage> errors = schema.validate(jsonNode);
        if (!errors.isEmpty()) {
            String details = errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.joining("; "));
            throw new IllegalArgumentException("Claude response failed JSON Schema validation: " + details);
        }
    }
}
