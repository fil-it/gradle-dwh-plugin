package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Элемент изменения - создание view
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(as = ChangeElementCreateView.class)
public class ChangeElementCreateView implements Change {

    /**
     * информация о создаваемой view
     */
    private ChangeCreateView createView;

    @Override
    public String getSchemaName() {
        return createView.getSchemaName();
    }
}
