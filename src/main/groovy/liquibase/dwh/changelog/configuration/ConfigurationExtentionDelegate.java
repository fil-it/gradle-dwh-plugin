package liquibase.dwh.changelog.configuration;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;

import groovy.lang.Closure;

public class ConfigurationExtentionDelegate {
    CopySpec baseCopySpec;

    private Project project;

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

    public CopySpec copyTemplate(Action<? super CopySpec> spec) {
        baseCopySpec = project.copySpec(spec);
        return baseCopySpec;
    }

    public CopySpec copyTemplate(Closure closure) {
        baseCopySpec = project.copySpec(closure);
        return baseCopySpec;
    }
}
