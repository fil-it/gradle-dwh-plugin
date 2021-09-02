package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конкретное изменение в виде sql-файла
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeSqlFile implements Change {

    /**
     * Изменение в sql-файле
     */
    private ChangeFile sqlFile;

    @Override
    public String getSchemaName() {
        return null;
    }

}
