package liquibase.dwh.changelog.configuration

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

/**
 * Расширение для конфигурирования плагина
 */
class ChangelogPluginExtension {
    /**
     * Именованные контейнеры для описания активностей liquibase в исходниках
     */
    private final NamedDomainObjectContainer<SubjectAreaConfiguration> subjectAreaConfigurations

    private final Project project

    /**
     * Конструктор
     *
     * @param project проект к которому применяется плагин
     */
    ChangelogPluginExtension(Project project) {
        this.project = project
        def delegate = new ConfigurationExtentionDelegate(project)
        subjectAreaConfigurations = project.container(SubjectAreaConfiguration.class,
                { new SubjectAreaConfiguration(it, project.copySpec(), delegate) })
        project.getExtensions().add('dwh', delegate)
        project.getExtensions().add("changelog", subjectAreaConfigurations)
    }


    NamedDomainObjectContainer<SubjectAreaConfiguration> getSubjectAreaConfigurations() {
        return subjectAreaConfigurations;
    }

    @Override
    String toString() {
        return "ChangelogPluginExtension{" +
                "subjectAreaConfigurations=" + subjectAreaConfigurations +
                ", project=" + project +
                '}';
    }
}




