package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.Collection;

/**
 * Файл списка изменений
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChangelogFile {

    /**
     * Элементы файла
     */
    @Singular("element")
    private Collection<? extends ChangelogElement> databaseChangeLog;

}


