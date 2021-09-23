package liquibase.dwh.changelog.configuration;

import lombok.Getter;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;

import groovy.lang.Closure;

/**
 * ConfigurationExtentionDelegate
 */
public class ConfigurationExtentionDelegate {

    /**
     * Gradle copy spec
     */
    @Getter
    private CopySpec baseCopySpec;

    /**
     * Проект
     */
    private final Project project;

    /**
     * Конфигурация
     * @param project
     */
    public ConfigurationExtentionDelegate(Project project) {
        this.project = project;
        baseCopySpec = project.copySpec(copySpec -> {
            copySpec.from(".");
            copySpec.include("gradlew");
            copySpec.include("gradlew.bat");
            copySpec.include("gradle/");
            copySpec.include("gradle.template.properties");
            copySpec.include("settings.gradle");
        });
    }

    /**
     * Получить CopySpec
     * @param spec
     * @return CopySpec
     */
    public CopySpec copyTemplate(Action<? super CopySpec> spec) {
        baseCopySpec = project.copySpec(spec);
        return baseCopySpec;
    }

    /**
     * Получить CopySpec
     * @param closure
     * @return CopySpec
     */
    public CopySpec copyTemplate(Closure closure) {
        baseCopySpec = project.copySpec(closure);
        return baseCopySpec;
    }
}
