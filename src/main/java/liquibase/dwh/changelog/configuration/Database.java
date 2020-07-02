package liquibase.dwh.changelog.configuration;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Объект для описания исходного кода, связанного с одной активностью lb
 */
@Data
@Accessors(fluent = true)
public class Database {
    /**
     * Название БД
     */
    private String name;

    /**
     * Расположение корня исходников
     */
    private String location;

    /**
     * Тип СУБД
     */
    private String type;

    /**
     * Флаг системности БД
     */
    private boolean system = false;

    /**
     * конструктор
     *
     * @param name имя активности
     */
    public Database(String name) {
        this.name = name;
    }

    /**
     * @return Расположение корня исходников
     */
    public String location() {
        if (location == null) {
            return getDefaultLocation();
        }
        return location;
    }

    /**
     * @return Расположение по-умолчанию
     */
    private String getDefaultLocation() {
        if ("gp".equals(type)) {
            return "src/main/gp/databases/" + name;
        } else if ("ch".equals(type)) {
            return "src/main/ch/schemas/" + name;
        } else {
            throw new IllegalStateException("Unexpected value: " + type);
        }
    }
}
