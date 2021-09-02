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
@Builder
public class ChangelogFile {

    /**
     * Элементы файла
     */
    @Singular("element")
    private Collection<? extends ChangelogElement> databaseChangeLog;

    /**
     * Получить билдер из объекта
     * @return билдер
     */
    public ChangelogFileBuilder toBuilder() {
        return new ChangelogFileBuilder().databaseChangeLog(this.databaseChangeLog == null ? java.util.Collections.emptyList() : this.databaseChangeLog);
    }

}


