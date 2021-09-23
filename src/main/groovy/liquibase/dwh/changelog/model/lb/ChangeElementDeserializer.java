package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gradle.api.GradleException;

import java.io.IOException;

/**
 * Кастомизация десериализации для возможности использовать интерфейсный тип вместо конкретного в классах модели.
 */
public class ChangeElementDeserializer extends JsonDeserializer<Change> {
    @Override
    public Change deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        if (root.has("createTable")) {
            return mapper.readValue(root.toString(), ChangeElementCreateTable.class);
        } else if (root.has("createView")) {
            return mapper.readValue(root.toString(), ChangeElementCreateView.class);
        } else if (root.has("createIndex")) {
            return mapper.readValue(root.toString(), ChangeElementCreateIndex.class);
        } else {
            throw new GradleException("Can't deserialize " + root);
        }
    }
}
