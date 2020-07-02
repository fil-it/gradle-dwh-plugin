package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент файла набора изменений - изменение
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonDeserialize(as = ChangelogElementChangeSet.class)
public class ChangelogElementChangeSet implements ChangelogElement {
    /**
     * Изменение
     */
    private ChangeSet changeSet;
}
