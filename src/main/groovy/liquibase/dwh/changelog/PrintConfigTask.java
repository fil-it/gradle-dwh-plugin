package liquibase.dwh.changelog;

import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * todo
 */
public class PrintConfigTask extends DefaultTask {
    /**
     * todo
     */
    private ChangelogPluginExtension config;

    {
        setGroup("DWH");
    }

    /**
     * Точка входа в задачу
     */
    @TaskAction
    public void action() {
        this.config = getProject().getExtensions().findByType(ChangelogPluginExtension.class);
        getLogger().quiet(config.toString());
    }

}
