package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Блок - создание view
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeCreateView {

    /**
     * имя каталога
     */
    private String catalogName;

    /**
     * Название схемы
     */
    private String schemaName;

    /**
     * Название view
     */
    private String viewName;

    /**
     * Комментарий
     */
    private String remarks;

    /**
     * Set to true if selectQuery is the entire view definition. False if the CREATE VIEW header should be added
     */
    private Boolean fullDefinition;

    /**
     * SQL запрос
     */
    private String selectQuery;

}
