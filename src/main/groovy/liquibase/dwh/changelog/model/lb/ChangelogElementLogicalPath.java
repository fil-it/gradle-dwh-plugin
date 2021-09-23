package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Логичейский путь списка изменений
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonDeserialize(as = ChangelogElementLogicalPath.class)
public class ChangelogElementLogicalPath implements ChangelogElement {
    /**
     * логический путь к файлу (любое значение, защищает от переноса и переименования)
     */
    private String logicalFilePath;

}
