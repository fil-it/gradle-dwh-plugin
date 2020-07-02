package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Конкретное изменение в виде sql-файла
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeSqlFile implements Change {
    /**
     * Изменение в sql-файле
     */
    private ChangeFile sqlFile;
}
