package liquibase.dwh.changelog.model.lb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.util.List;

/**
 * Описание изменения
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeSet {
    /**
     * идентификатор изменения
     */
    private String id;

    /**
     * имя автора изменения
     */
    private String author;

    /**
     * список изменений
     */
    @Singular
    private List<? extends Change> changes;

    /**
     * список изменений, описывающих откат
     */
    @Singular("rollback")
    private List<? extends Change> rollback;

}
