package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Включение файла в списка изменений
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangelogInclude {
    /**
     * путь к файлу
     */
    private String file;

    /**
     * флаг относительный путь или нет
     */
    private Boolean relativeToChangelogFile;

}
