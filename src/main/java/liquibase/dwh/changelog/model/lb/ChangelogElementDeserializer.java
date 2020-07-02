package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.gradle.api.GradleException;

import java.io.IOException;

/**
 * Кастомизация десериализации для возможности использовать интерфейсный тип вместо конкретного в классах модели.
 */
public class ChangelogElementDeserializer extends JsonDeserializer<ChangelogElement> {
    @Override
    public ChangelogElement deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        ObjectNode root = mapper.readTree(jp);
        if (root.has("logicalFilePath")) {
            return mapper.readValue(root.toString(), ChangelogElementLogicalPath.class);
        } else if (root.has("include")) {
            return mapper.readValue(root.toString(), ChangelogElementInclude.class);
        } else if (root.has("changeset")) {
            return mapper.readValue(root.toString(), ChangelogElementChangeSet.class);
        } else {
            throw new GradleException("Can't deserialize " + root);
        }
    }
}
