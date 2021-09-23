package liquibase.dwh.changelog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.internal.file.copy.DefaultCopySpec;
import org.gradle.api.tasks.TaskAction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import liquibase.dwh.changelog.configuration.SubjectAreaConfiguration;
import liquibase.dwh.changelog.model.lb.ChangeFile;
import liquibase.dwh.changelog.model.lb.ChangeSet;
import liquibase.dwh.changelog.model.lb.ChangeSqlFile;
import liquibase.dwh.changelog.model.lb.ChangelogElement;
import liquibase.dwh.changelog.model.lb.ChangelogElementChangeSet;
import liquibase.dwh.changelog.model.lb.ChangelogElementInclude;
import liquibase.dwh.changelog.model.lb.ChangelogElementLogicalPath;
import liquibase.dwh.changelog.model.lb.ChangelogFile;
import liquibase.dwh.changelog.model.lb.ChangelogInclude;

/**
 * Задача генерации списков изменения на основе созданных изменений
 */
public class GenerateChangelogTask extends DefaultTask {
    /**
     * название задачи
     */
    private String taskName;

    /**
     * автор
     */
    private String author;

    /**
     * конфигурации плагина
     */
    private ChangelogPluginExtension changelogPluginExtension;

    {
        setGroup("DWH");
    }

    /**
     * Подготовка
     */
    private void init() {
        changelogPluginExtension = getProject().getExtensions().findByType(ChangelogPluginExtension.class);
    }

    /**
     * Точка входа задачи
     */
    @TaskAction
    public void action() {
        init();

        this.taskName = GitMetaProvider.extractTaskName(getProject().getRootDir(), getLogger());
        this.author = GitMetaProvider.extractAuthor(getProject().getRootDir(), getLogger());

        processSubjectAreas();
    }

    /**
     * Обработка каждой предметной области
     */
    private void processSubjectAreas() {
        changelogPluginExtension.getSubjectAreaConfigurations().forEach(this::processSubjectArea);
    }

    /**
     * TODO
     * @return CopySpecInternal
     */
    private CopySpecInternal createRootSpec() {
        return this.getProject().getObjects().newInstance(DefaultCopySpec.class, new Object[0]);
    }

    /**
     * Получить источники из конфига
     * @param subjectArea конфигурация предметной области
     * @return список файлов найденных по конфигу
     */
    private FileCollection getSources(SubjectAreaConfiguration subjectArea) {
        return ((CopySpecInternal) createRootSpec().with(subjectArea.getSourcesCopySpec())).buildRootResolver().getAllSource();
    }

    /**
     * Обработка предметной области
     * @param subjectArea конфигурация предметной области
     */
    private void processSubjectArea(SubjectAreaConfiguration subjectArea) {
        getLogger().quiet("Processing activity '{}' with master-changelog '{}'", subjectArea.getName(), subjectArea.getChangelogFile());
        FileCollection sources = getSources(subjectArea);
        Path masterFile = getProjectPath(Paths.get(subjectArea.getChangelogFile()));
        Path tasksDir = findTaskChangelogDir(masterFile, subjectArea.getName());
        if (!sources.isEmpty()) {
            getLogger().debug("Sources paths for subject area '{}': {}", subjectArea.getName(), sources);
        } else {
            getLogger().warn("Sources for subject area {} is empty. Stop processing.", subjectArea.getName());
            return;
        }
        validateTaskChangelog(tasksDir, taskName, subjectArea.getName());
        Path taskChangelogFilePath = generateTaskChangelog(tasksDir, sources.getFiles());

        Path normalizedPath = masterFile.getParent().relativize(taskChangelogFilePath).normalize();
        getLogger().quiet("Try to link task file {} into master changelog {}", normalizedPath, masterFile);
        addIncludeToMaster(normalizedPath, masterFile);
    }

    /**
     * Добавить блок include в master.yaml
     * @param normalizedPath путь для указания в блоке
     * @param masterFile путь до master.yaml
     */
    private void addIncludeToMaster(Path normalizedPath, Path masterFile) {
        ChangelogElementInclude include = ChangelogElementInclude.builder()
                                                                 .include(ChangelogInclude.builder()
                                                                                          .relativeToChangelogFile(true)
                                                                                          .file(pathToString(normalizedPath))
                                                                                          .build())
                                                                 .build();
        try {
            ChangelogFile master = validateMasterAndAppendIncludeIfNeeded(masterFile, include);
            overwriteMaster(masterFile, master);
        } catch (IOException e) {
            throw new GradleException("Can't transform include object to YAML string and write in to file", e);
        }

    }

    /**
     * Перезапись master.yaml
     * @param masterFile путь до master.yaml
     * @param master новый файл
     * @throws IOException
     */
    private void overwriteMaster(Path masterFile, ChangelogFile master) throws IOException {
        getObjectMapper().writeValue(getProject().file(masterFile), master);
    }

    /**
     * Проверяет master.yaml и добавляет в него блок include если это необходимо
     * @param masterFile путь до master.yaml
     * @param include Блок include
     * @return master.yaml
     * @throws IOException
     */
    private ChangelogFile validateMasterAndAppendIncludeIfNeeded(Path masterFile, ChangelogElementInclude include) throws IOException {
        String content = String.join("\n", Files.readAllLines(getProject().file(masterFile).toPath()));
        ChangelogFile master = getObjectMapper().readValue(content, ChangelogFile.class);
        AtomicInteger taskMentionCount = new AtomicInteger(0);
        Optional<ChangelogInclude> sameInclude = master.getDatabaseChangeLog().stream()
                                                       .filter(ChangelogElementInclude.class::isInstance)
                                                       .map(ChangelogElementInclude.class::cast)
                                                       .map(ChangelogElementInclude::getInclude)
                                                       .peek(in -> countMentions(taskMentionCount, in))
                                                       .filter(in -> in.getFile().equals(include.getInclude().getFile()))
                                                       .filter(in -> in.getRelativeToChangelogFile()
                                                                       .equals(include.getInclude().getRelativeToChangelogFile()))
                                                       .findFirst();
        if (taskMentionCount.get() > sameInclude.map(str -> 1).orElse(0)) {
            throw new GradleException("In master file found mentions of task " + taskName + " that not correspond generated include " + include);
        }
        if (!sameInclude.isPresent()) {
            getLogger().info("There is no include in master file for task {}. Append new include {}", taskName, include);
            return master.toBuilder()
                         .element(include)
                         .build();
        } else {
            getLogger().warn("There is already include section for task {} with same content as generated {}. Skip master modification", taskName, include);
            return master;
        }
    }

    /**
     * TODO
     * @param taskMentionCount
     * @param in
     */
    private void countMentions(AtomicInteger taskMentionCount, ChangelogInclude in) {
        if (in.getFile().contains(taskName)) {
            taskMentionCount.incrementAndGet();
        }
    }

    /**
     * Провалидировать чейнджлог для таски
     * @param tasksDir путь до чейнджлогов таски
     * @param taskName имя таски
     * @param name название activity
     */
    private void validateTaskChangelog(Path tasksDir, String taskName, String name) {
        List<Path> changelog = listFilesUnchecked(tasksDir)
                .filter(it -> it.getFileName().toString().contains(taskName))
                .collect(Collectors.toList());
        if (!changelog.isEmpty()) {
            String msg = String.format("Changelog for current task %s in activity '%s' already exists: %s. " +
                                               "Please delete it manually if you want to regenerate changelog",
                                       taskName,
                                       name,
                                       changelog);
            throw new GradleException(msg);
        }
        getLogger().quiet("There is no changelog file for taskName {} in activity '{}'. It's correct.", taskName, name);
    }

    /**
     * Найти папку с чейнджлогами таски
     * @param master путь до master.yaml
     * @param name название activity
     * @return Путь дол папки с чейнджлогами таски
     */
    private Path findTaskChangelogDir(Path master, String name) {
        Path tasksDir = master.getParent().resolve("tasks");
        if (getProject().file(tasksDir).isDirectory()) {
            getLogger().quiet("Tasks directory is {} for activity `{}`", tasksDir, name);
            return tasksDir;
        } else {
            String msg = "Can't find taskName's changelogs directory for activity " + name;
            getLogger().error(msg);
            throw new GradleException(msg);
        }
    }

    /**
     * TODO
     * @param it
     * @return Stream of Path
     */
    private Stream<Path> listFilesUnchecked(Path it) {
        try {
            return Files.list(getProject().file(it).toPath()).map(this::getProjectPath);
        } catch (IOException e) {
            throw new GradleException("Can't list files in source set directory", e);
        }
    }

    /**
     * Получить путь до проекта
     * All incoming paths are project relative, however gradle daemon working dir is not equal to projectDir
     * @param f
     * @return Путь до проекта
     */
    private Path getProjectPath(Path f) {
        return getProject().getProjectDir().toPath().relativize(f).normalize();
    }

    /**
     * Путь в строку
     * @param path путь
     * @return строка
     */
    private String pathToString(Path path) {
        return path.toString().replace('\\', '/');
    }

    /**
     * Сгенерировать чейнджлог для таски
     * @param tasksDir папка для чейнджлога
     * @param files sql файлы со скриптами
     * @return путь к чейнджлогу
     */
    private Path generateTaskChangelog(Path tasksDir, Set<File> files) {
        Path taskChangelogPath = generateFilePath(tasksDir);
        ChangelogElementLogicalPath pathElement = ChangelogElementLogicalPath.builder()
                                                                             .logicalFilePath(pathToString(taskChangelogPath))
                                                                             .build();
        ChangelogFile.ChangelogFileBuilder targetBuilder = ChangelogFile.builder();
        targetBuilder.element(pathElement);
        files.stream()
             .filter(it -> !it.getName().contains("UD") && !it.getName().contains("UM"))
             .map(it -> createFileChangelogRepresentation(it, files))
             .forEach(targetBuilder::element);
        ObjectMapper mapper = getObjectMapper();
        try {
            mapper.writeValue(taskChangelogPath.toFile(), targetBuilder.build());
            return taskChangelogPath;
        } catch (IOException e) {
            throw new GradleException("Can't convert changelog to YAML file", e);
        }
    }

    /**
     * Получить маппер
     * @return маппер
     */
    private ObjectMapper getObjectMapper() {
        YAMLFactory jf = new YAMLFactory();
        jf.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        jf.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(jf);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * TODO
     * @param file
     * @param files
     * @return ChangelogElement
     */
    private ChangelogElement createFileChangelogRepresentation(File file, Collection<File> files) {
        if (file.toString().contains("CD") || file.toString().contains("CM")) {
            return ChangelogElementInclude.builder()
                                          .include(ChangelogInclude.builder()
                                                                   .file(pathToString(file.toPath()))
                                                                   .build())
                                          .build();
        }
        ChangeSet changeset = createChangeSet(file, files);
        return ChangelogElementChangeSet.builder()
                                        .changeSet(changeset)
                                        .build();
    }

    /**
     * Сгенерировать блок changeSet
     * @param file
     * @param files
     * @return ChangeSet
     */
    private ChangeSet createChangeSet(File file, Collection<File> files) {
        Path rollback = findRollbackPath(file, files);
        ChangeSet.ChangeSetBuilder builder = ChangeSet.builder();
        builder.author(author)
               .id(file.getName().replace("DD", "CD").replace("DM", "CM").replace(".sql", ""))
               .change(ChangeSqlFile.builder()
                                    .sqlFile(ChangeFile.builder()
                                                       .path(pathToString(file.toPath()))
                                                       .encoding("UTF-8")
                                                       .build())
                                    .build());
        if (rollback != null) {
            builder.rollback(ChangeSqlFile.builder()
                                          .sqlFile(ChangeFile.builder()
                                                             .path(pathToString(rollback))
                                                             .encoding("UTF-8")
                                                             .build())
                                          .build());
        }
        return builder.build();
    }

    /**
     * TODO
     * @param file
     * @param files
     * @return Path
     */
    @Nullable
    private Path findRollbackPath(File file, Collection<File> files) {
        Path rollbackCandidate = file.toPath().getParent().resolve(file.getName().replace("DD", "UD").replace("DM", "UM"));
        if (files.contains(rollbackCandidate.toFile())) {
            return rollbackCandidate;
        } else {
            getLogger().warn("Can't find rollback file for {}", file);
            return null;
        }
    }

    /**
     * Сгенерировать путь до чейнджлога таски
     * @param taskDir путь с файлами таски
     * @return путь до чейнджлога таски
     */
    private Path generateFilePath(Path taskDir) {
        return taskDir.resolve(taskName + ".yaml");
    }

}
