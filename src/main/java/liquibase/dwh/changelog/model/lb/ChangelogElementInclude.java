package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент списка изменений - включение другого списка или изменения
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonDeserialize(as = ChangelogElementInclude.class)
public class ChangelogElementInclude implements ChangelogElement {
    /**
     * Описание включение другого файла
     */
    private ChangelogInclude include;
}
