package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент изменения - создание таблицы
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(as = ChangeElementCreateTable.class)
public class ChangeElementCreateTable implements Change {

    /**
     * информация о создаваемой таблице
     */
    private ChangeCreateTable createTable;

    @Override
    public String getSchemaName() {
        return createTable.getSchemaName();
    }
}
