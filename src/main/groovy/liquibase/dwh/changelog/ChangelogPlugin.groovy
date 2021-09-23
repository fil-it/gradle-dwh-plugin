package liquibase.dwh.changelog

import liquibase.dwh.changelog.configuration.ChangelogPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip
import org.liquibase.gradle.Activity
import org.liquibase.gradle.LiquibaseExtension
import org.liquibase.gradle.LiquibasePlugin

/**
 * Точка входа плагина
 */
class ChangelogPlugin implements Plugin<Project> {

    private ChangelogPluginExtension changelogPluginExtension

    @Override
    void apply(Project project) {
        //Добавление нового метода к существующему классу
        Activity.metaClass.extraProperty { String key, String value ->
            (delegate as Activity).setProperty(key, value)
        }
        applyPlugins(project)
        applyExtensions(project)
        applyTasks(project)
        //Доконфигурирование плагина после прочтения информации из билд-скрипт
        project.afterEvaluate { eprj ->
            configureExtensions(eprj)
            generateDynamicTasks(eprj)
        }
    }

    /**
     * Доконфигурирование расширений проекта
     * @param eprj проект на стадии afterEvaluate
     * @return
     */
    private configureExtensions(eprj) {
        projectSubjectAreaToActivities(eprj)
    }

    /**
     * Создание статических задач для плагина
     * @param project в котором создаются задачи
     */
    private def applyTasks(Project project) {
        project.getTasks().create("generateChangelogFiles", GenerateChangelogTask.class)
        project.getTasks().create("printConfig", PrintConfigTask.class)
        project.getTasks().create("importSql", ImportSqlTask.class)
        project.getTasks().create("generateSeparateChangelog", GenerateSeparateChangelogTask.class)
    }

    /**
     * Применения транзитивных плагинов
     * @param project к которому применяются плагины
     */
    private def applyPlugins(Project project) {
        project.getPlugins().apply(LiquibasePlugin.class)
    }

    /**
     * Создание динамических задач, количество которых зависит от конфигурации проекта
     * @param eprj проект на стадии afterEvaluate
     */
    private void generateDynamicTasks(Project eprj) {
        changelogPluginExtension.getSubjectAreaConfigurations().forEach { database ->
            eprj.getTasks().create(database.getName() + "ReleaseArchive", Zip.class, { zip ->
                zip.with(database.getSourcesCopySpec());
                zip.setProperty("archiveBaseName", database.getName());
                zip.setGroup("DWH");
            })
        }
    }

    /**
     * Регистрация расширений
     * @param project в котором регистрируются расширения
     */
    private void applyExtensions(Project project) {
        changelogPluginExtension = project.getExtensions().create("changelogPluginExtension", ChangelogPluginExtension.class, project);
    }

    /**
     * Регистрация активностей, которые сконфигурированы в {@link liquibase.dwh.changelog.configuration.SubjectAreaConfiguration} в расширении Liquibase
     * @param project проект с расширениями
     */
    private void projectSubjectAreaToActivities(Project project) {
        LiquibaseExtension lb = project.getExtensions().findByType(LiquibaseExtension.class);
        changelogPluginExtension.getSubjectAreaConfigurations().stream().forEach { database ->
            lb.getActivities().register(database.getName(), { activity ->
                activity.setParameters(database.getActivity().getParameters());
                activity.setArguments(database.getActivity().getArguments());
            })
        }
        lb.getActivities();
    }
}
