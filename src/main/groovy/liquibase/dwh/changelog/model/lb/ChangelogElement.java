package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Элемент набора изменений
 */
@JsonDeserialize(using = ChangelogElementDeserializer.class)
public interface ChangelogElement {
}
