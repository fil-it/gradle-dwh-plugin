package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * Блок - создание таблицы
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCreateTable {

    /**
     * имя каталога
     */
    private String catalogName;

    /**
     * Комментарий
     */
    private String remarks;

    /**
     * Название схемы
     */
    private String schemaName;

    /**
     * имя таблицы
     */
    private String tableName;

    /**
     * Колонки
     */
    private Collection<Object> columns;

}
