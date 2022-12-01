package liquibase.dwh.changelog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import liquibase.dwh.changelog.configuration.SubjectAreaConfiguration;
import liquibase.dwh.changelog.model.lb.ChangeSet;
import liquibase.dwh.changelog.model.lb.ChangelogElement;
import liquibase.dwh.changelog.model.lb.ChangelogElementChangeSet;
import liquibase.dwh.changelog.model.lb.ChangelogFile;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Таска берет полный чейнджлог (master.yaml) liquibase и нарезает его на составные части для каждой схемы.
 */
public class GenerateSeparateChangelogTask extends DefaultTask {

    {
        setGroup("DWH");
    }

    /**
     * название задачи
     */
    private String taskName;

    /**
     * конфигурации плагина
     */
    private ChangelogPluginExtension changelogPluginExtension;

    /**
     * Подготовка к выполнению таски
     */
    private void init() {
        changelogPluginExtension = getProject().getExtensions().findByType(ChangelogPluginExtension.class);
    }

    /**
     * Точка входа
     */
    @TaskAction
    public void action() {
        init();

        this.taskName = GitMetaProvider.extractTaskName(getProject().getRootDir(), getLogger());

        processSubjectAreas();
    }

    /**
     * Обработать все предметные области
     */
    private void processSubjectAreas() {
        changelogPluginExtension.getSubjectAreaConfigurations().forEach(this::processSubjectArea);
    }

    /**
     * Обработка предметной области
     *
     * @param subjectArea предметная область
     */
    private void processSubjectArea(SubjectAreaConfiguration subjectArea) {
        getLogger().quiet("Processing activity '{}' with master-changelog '{}'", subjectArea.getName(), subjectArea.getChangelogFile());

        Path masterFile = Paths.get(subjectArea.getChangelogFile());
        ChangelogFile changelog = null;
        try {
            changelog = readChangelogFile(masterFile);
            getLogger().quiet("Loaded changelog file with {} changes", changelog.getDatabaseChangeLog().size());
        } catch (IOException e) {
            getLogger().error("Changelog file not found on path {}", subjectArea.getChangelogFile());
            return;
        }

        List<ChangeSet> changeSetList = changelog.getDatabaseChangeLog().stream()
                .filter(ChangelogElementChangeSet.class::isInstance)
                .map(ChangelogElementChangeSet.class::cast)
                .map(ChangelogElementChangeSet::getChangeSet)
                .collect(Collectors.toList());

        Map<String, ChangelogFile.ChangelogFileBuilder> changesForSchemaMap = new HashMap<>();

        changeSetList.forEach(changeSet -> {
            String schemaName = changeSet.getChanges().get(0).getSchemaName();

            ChangelogFile.ChangelogFileBuilder builder = changesForSchemaMap.get(schemaName);
            if (builder == null) {
                builder = ChangelogFile.builder();
            }
            ChangelogElement element = ChangelogElementChangeSet.builder()
                    .changeSet(changeSet)
                    .build();

            builder.element(element);
            changesForSchemaMap.put(schemaName, builder);

        });

        changesForSchemaMap.forEach((schemaName, changelogFileBuilder) -> saveSchemaChangelog(schemaName, masterFile, changelogFileBuilder));

    }

    /**
     * Записать на диск чейнджлог схемы
     *
     * @param schemaName           Название схемы
     * @param masterChangelogPath  Путь до мастер-чейнджлога (master.yaml)
     * @param changelogFileBuilder Билдер с данными для записи
     */
    private void saveSchemaChangelog(String schemaName, Path masterChangelogPath, ChangelogFile.ChangelogFileBuilder changelogFileBuilder) {

        Path schemaChangelogPath = null;
        try {
            schemaChangelogPath = getSchemaChangelogPath(masterChangelogPath, schemaName);
            getLogger().quiet("Schema: '{}', target path: '{}'", schemaName, schemaChangelogPath);
        } catch (IOException e) {
            getLogger().error("Error on create changelog file for schema '{}'", schemaName);
        }

        ObjectMapper mapper = getObjectMapper();
        try {
            mapper.writeValue(schemaChangelogPath.toFile(), changelogFileBuilder.build());
        } catch (IOException e) {
            throw new GradleException("Can't convert changelog to YAML file", e);
        }

    }

    /**
     * Вычисление пути чейнджлога для схемы
     *
     * @param master     Путь до мастер чейнджлога (master.yaml)
     * @param schemaName Название схемы
     * @return Путь до чейнджлога конкретной схемы
     * @throws IOException
     */
    private Path getSchemaChangelogPath(Path master, String schemaName) throws IOException {
        Path schemaDir = master.getParent().getParent().resolve("schemas").resolve(schemaName);
        if (getProject().file(schemaDir).isDirectory()) {
            Path schemaChangelogPath = schemaDir.resolve("1-0-0-CD-" + taskName + "-changelog.yaml");
            if (!schemaChangelogPath.toFile().exists()) {
                schemaChangelogPath.toFile().createNewFile();
            }
            return schemaChangelogPath;
        } else {
            String msg = "Can't find schema directory for schema " + schemaName;
            getLogger().error(msg);
            throw new GradleException(msg);
        }
    }

    /**
     * Прочитать мастер-чейнджлог (master.yaml) в структуру ChangelogFile
     *
     * @param masterFilePath Путь до master.yaml
     * @return ChangelogFile
     * @throws IOException
     */
    private ChangelogFile readChangelogFile(Path masterFilePath) throws IOException {
        String content = String.join("\n", Files.readAllLines(getProject().file(masterFilePath).toPath()));
        return getObjectMapper().readValue(content, ChangelogFile.class);
    }

    /**
     * Получить мапер
     *
     * @return мапер
     */
    private ObjectMapper getObjectMapper() {
        YAMLFactory jf = new YAMLFactory();
        jf.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        jf.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(jf);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

}
