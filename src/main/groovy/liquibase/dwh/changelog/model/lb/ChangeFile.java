package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Путь к файлу с чейнжсетом
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeFile {
    /**
     * кодировка файла
     */
    private String encoding;

    /**
     * путь к файлу
     */
    private String path;

}
