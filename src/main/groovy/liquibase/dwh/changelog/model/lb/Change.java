package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * change in yaml file
 */
@JsonDeserialize(using = ChangeElementDeserializer.class)
public interface Change {

    /**
     * Получить имя схемы
     * @return имя схемы
     */
    String getSchemaName();
}
