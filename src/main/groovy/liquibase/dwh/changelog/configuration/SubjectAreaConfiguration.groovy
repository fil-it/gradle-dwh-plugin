package liquibase.dwh.changelog.configuration

import org.gradle.api.Action
import org.gradle.api.file.CopySpec
import org.gradle.util.ConfigureUtil
import org.liquibase.gradle.Activity

/**
 * Объект "Предметная область" для описания исходного кода, связанного с одной активностью lb
 */
class SubjectAreaConfiguration {
    /**
     * Ключ свойств для хранения пути к файлу-чейнжлогу
     */
    private static CHANGELOG_FILE_ARG_NAME = "changeLogFile"

    /**
     * Название БД
     */
    String name

    /**
     * Соответствующая активность Liquibase
     */
    Activity activity

    /**
     * Спецификация, определяющая список всех исходников предметной области
     */
    CopySpec sourcesCopySpec

    ConfigurationExtentionDelegate configuration

    /**
     * конструктор
     *
     * @param name имя активности
     */
    SubjectAreaConfiguration(String name,
                             CopySpec copySpec,
                             ConfigurationExtentionDelegate configuration) {
        this.name = name
        activity = new Activity(name)
        activity.labels(name)
        this.sourcesCopySpec = copySpec
        this.configuration = configuration
    }

    /**
     * Конфигурирование исходники с помощью замыкания
     * @param config замыкание с конфигураций {@link CopySpec}
     */
    void sources(Closure config) {
        ConfigureUtil.configure(config, this.sourcesCopySpec)
    }

    /**
     * Конфигурирование исходников с помощью {@link Action}
     * @param config конфигурация {@link CopySpec}
     */
    void sources(Action<? super CopySpec> config) {
        config.execute(sourcesCopySpec)
    }

    /**
     * Конфигурирования активности Liquibase с помощью замыкания
     * @param config замыкание-конфигурация {@link Activity}
     */
    void activity(Closure config) {
        ConfigureUtil.configure(config, this.activity)
    }

    /**
     * Конфигурирования активности Liquibase с помощью {@link Action}
     * @param config конфигурация {@link Activity}
     */
    void activity(Action<? super Activity> config) {
        config.execute(this.activity)
    }

    /**
     * Делегат к аргументам {@link Activity} по ключу {@link #CHANGELOG_FILE_ARG_NAME}
     * @return путь к чейнжлог-файлу или null, если такой аргумент не задан
     */
    String getChangelogFile() {
        activity.arguments[CHANGELOG_FILE_ARG_NAME]
    }

    CopySpec getSourcesCopySpec() {
        return sourcesCopySpec.with(configuration.baseCopySpec)
    }
}
