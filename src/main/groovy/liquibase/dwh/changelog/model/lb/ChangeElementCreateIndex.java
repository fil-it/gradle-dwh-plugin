package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент изменения - создание индекса
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = ChangeElementCreateIndex.class)
public class ChangeElementCreateIndex implements Change {

    /**
     * информация о создаваемом индексе
     */
    private ChangeCreateIndex createIndex;

    @Override
    public String getSchemaName() {
        return createIndex.getSchemaName();
    }
}
