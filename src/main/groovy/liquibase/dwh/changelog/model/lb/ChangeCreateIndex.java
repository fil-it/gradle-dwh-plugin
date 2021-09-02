package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * Блок - создание индекса
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCreateIndex {

    /**
     * Название каталога
     */
    private String catalogName;

    /**
     * Название схемы
     */
    private String schemaName;

    /**
     * имя таблицы
     */
    private String tableName;

    /**
     * имя индекса
     */
    private String indexName;

    /**
     * Колонки
     */
    private Collection<Object> columns;

}
