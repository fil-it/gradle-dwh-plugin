package liquibase.dwh.changelog;

import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Точка входа плагина
 */
public class ChangelogPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getPlugins().apply(org.liquibase.gradle.LiquibasePlugin.class);
        project.getTasks().create("generateChangelogFiles", GenerateChangelogTask.class);
        project.getExtensions().create("changelogPluginExtension", ChangelogPluginExtension.class, project);
        project.getTasks().create("printConfig", PrintConfigTask.class);
        project.getTasks().create("importSql", ImportSqlTask.class);
    }
}
