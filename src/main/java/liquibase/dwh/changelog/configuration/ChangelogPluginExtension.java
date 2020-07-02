package liquibase.dwh.changelog.configuration;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Расширение для конфигурирования плагина
 */
public class ChangelogPluginExtension {
    /**
     * Именованные контейнеры для описания активностей liquibase в исходниках
     */
    private final NamedDomainObjectContainer<Database> greenPlumContainer;

    /**
     * Конструктор
     *
     * @param project проект к которому применяется плагин
     */
    public ChangelogPluginExtension(Project project) {
        greenPlumContainer = project.container(Database.class);
        project.getExtensions().add("changelog", greenPlumContainer);
    }

    /**
     * Проверка активности на системность по имени
     *
     * @param name имя активности
     * @return true для системной активности и false в противном
     */
    public boolean isSystemChangelog(String name) {
        return Optional
                .ofNullable(greenPlumContainer.findByName(name))
                .map(Database::system)
                .orElse(false);
    }

    public Map<String, String> getFileMap() {
        return greenPlumContainer.stream().collect(Collectors.toMap(Database::name, this::getDatabaseFilePath));
    }

    private String getDatabaseFilePath(Database database) {
        return database.location() + "/_changelogs/master.yaml";
    }

    @Override
    public String toString() {
        return greenPlumContainer.stream().map(Objects::toString).collect(Collectors.joining("\n\t", "ChangelogPluginExtension{\n\t", "\n}\n"));
    }
}




