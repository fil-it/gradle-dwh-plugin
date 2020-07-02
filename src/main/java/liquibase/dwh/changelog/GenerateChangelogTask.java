package liquibase.dwh.changelog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import liquibase.dwh.changelog.model.lb.ChangeFile;
import liquibase.dwh.changelog.model.lb.ChangeSet;
import liquibase.dwh.changelog.model.lb.ChangeSqlFile;
import liquibase.dwh.changelog.model.lb.ChangelogElement;
import liquibase.dwh.changelog.model.lb.ChangelogElementChangeSet;
import liquibase.dwh.changelog.model.lb.ChangelogElementInclude;
import liquibase.dwh.changelog.model.lb.ChangelogElementLogicalPath;
import liquibase.dwh.changelog.model.lb.ChangelogFile;
import liquibase.dwh.changelog.model.lb.ChangelogInclude;
import lombok.var;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.liquibase.gradle.LiquibaseExtension;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private ChangelogPluginExtension config;

    {
        setGroup("DWH");
    }

    private void init() {
        config = getProject().getExtensions().findByType(ChangelogPluginExtension.class);
    }

    /**
     * Точка входа задачи
     */
    @TaskAction
    public void action() {
        init();
        var activities = new HashMap<String, String>();
        fillActivitiesFromConfig(activities);

        this.taskName = GitMetaProvider.extractTaskName(getProject().getRootDir(), getLogger());
        this.author = GitMetaProvider.extractAuthor(getProject().getRootDir(), getLogger());

        processActivities(activities);
    }

    private void processActivities(Map<String, String> activities) {
        activities.forEach(this::processActivity);
    }

    private void processActivity(String name, String file) {
        var isSystem = config.isSystemChangelog(name);
        getLogger().quiet("Processing {}activity '{}' with master-changelog '{}'", isSystem ? "system " : "", name, file);
        var masterFile = getProjectPath(getProject().file(file));
        var tasksDir = findTaskChangelogDir(masterFile, name);
        var paths = determineSearchPaths(masterFile, isSystem);
        if (paths != null) {
            getLogger().quiet("Search paths for activity '{}': {}", name, paths);
        } else {
            getLogger().warn("Search paths is empty. Stop processing.");
            return;
        }
        validateTaskChangelog(tasksDir, taskName, name);
        var taskFiles = collectFiles(paths);
        if (taskFiles.isEmpty()) {
            getLogger().warn("No changesets found for taskName {} in activity {}. Search paths: {}. Stop processing.", taskName, name, paths);
            return;
        } else {
            getLogger().quiet("{} changesets found for taskName {} in activity {}: {}", taskFiles.size(), taskName, name, taskFiles);
        }
        var taskChangelogFilePath = generateTaskChangelog(tasksDir, taskFiles);

        Path normalizedPath = masterFile.getParent().relativize(taskChangelogFilePath).normalize();
        getLogger().quiet("Try to link task file {} into master changelog {}", normalizedPath, masterFile);
        addIncludeToMaster(normalizedPath, masterFile);

    }

    private void addIncludeToMaster(Path normalizedPath, Path masterFile) {
        var include = ChangelogElementInclude.builder()
                                             .include(ChangelogInclude.builder()
                                                                      .relativeToChangelogFile(true)
                                                                      .file(normalizedPath.toString())
                                                                      .build())
                                             .build();
        try {
            var master = validateMasterAndAppendIncludeIfNeeded(masterFile, include);
            overwriteMaster(masterFile, master);
        } catch (IOException e) {
            throw new GradleException("Can't transform include object to YAML string and write in to file", e);
        }

    }

    private void overwriteMaster(Path masterFile, ChangelogFile master) throws IOException {
        getObjectMapper().writeValue(getProject().file(masterFile), master);
    }

    private ChangelogFile validateMasterAndAppendIncludeIfNeeded(Path masterFile, ChangelogElementInclude include) throws IOException {
        String content = String.join("\n", Files.readAllLines(getProject().file(masterFile).toPath()));
        ChangelogFile master = getObjectMapper().readValue(content, ChangelogFile.class);
        var taskMentionCount = new AtomicInteger(0);
        Optional<ChangelogInclude> sameInclude = master.getDatabaseChangeLog().stream()
                                                       .filter(el -> el instanceof ChangelogElementInclude)
                                                       .map(el -> ((ChangelogElementInclude) el))
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

    private void countMentions(AtomicInteger taskMentionCount, ChangelogInclude in) {
        if (in.getFile().contains(taskName)) {
            taskMentionCount.incrementAndGet();
        }
    }

    private List<Path> collectFiles(List<Path> paths) {
        return paths.stream()
                    .flatMap(this::walkFilesUnchecked)
                    .filter(f -> getProject().file(f).isFile())
                    .filter(it -> it.getFileName().toString().contains(taskName))
                    .collect(Collectors.toList());
    }

    private void validateTaskChangelog(Path tasksDir, String taskName, String name) {
        var changelog = listFilesUnchecked(tasksDir)
                .filter(it -> it.getFileName().toString().contains(taskName))
                .collect(Collectors.toList());
        if (!changelog.isEmpty()) {
            var msg = String.format("Changelog for current task %s in activity '%s' already exists: %s. " +
                                            "Please delete it manually if you want to regenerate changelog",
                                    taskName,
                                    name,
                                    changelog);
            throw new GradleException(msg);
        }
        getLogger().quiet("There is no changelog file for taskName {} in activity '{}'. It's correct.", taskName, name);
    }

    private Path findTaskChangelogDir(Path master, String name) {
        var tasksDir = master.getParent().resolve("tasks");
        if (getProject().file(tasksDir).isDirectory()) {
            getLogger().quiet("Tasks directory is {} for activity `{}`", tasksDir, name);
            return tasksDir;
        } else {
            var msg = "Can't find taskName's changelogs directory for activity " + name;
            getLogger().error(msg);
            throw new GradleException(msg);
        }
    }

    private List<Path> determineSearchPaths(Path file, boolean isSystem) {
        var currentDbDirectory = file.getParent().getParent();
        var upperLevelDirectory = file.getParent().getParent().getParent();
        List<Path> res;

        res = listFilesUnchecked(currentDbDirectory)
                .filter(it -> !it.getFileName().toString().startsWith("_"))
                .collect(Collectors.toList());


        if (isSystem) {
            res.addAll(listFilesUnchecked(upperLevelDirectory)
                               .flatMap(this::listFilesUnchecked)
                               .filter(it -> it.getFileName().toString().equals("_init"))
                               .collect(Collectors.toList()));
        }
        return res;
    }

    private Stream<Path> listFilesUnchecked(Path it) {
        return getProject().files(it).getFiles().stream().map(this::getProjectPath);
    }

    private Stream<Path> walkFilesUnchecked(Path it) {
        return getProject().fileTree(it).getFiles().stream().map(this::getProjectPath);
    }

    // All incoming paths are project relative, however gradle daemon working dir is not equal to projectDir
    private Path getProjectPath(File f) {
        return getProject().getProjectDir().toPath().relativize(f.toPath()).normalize();
    }

    private Path generateTaskChangelog(Path tasksDir, Collection<Path> files) {
        var taskChangelogPath = generateFilePath(tasksDir);
        var pathElement = ChangelogElementLogicalPath.builder()
                                                     .logicalFilePath(taskChangelogPath.toString())
                                                     .build();
        var targetBuilder = ChangelogFile.builder();
        targetBuilder.element(pathElement);
        files.stream()
             .filter(it -> !it.getFileName().toString().contains("UD") && !it.getFileName().toString().contains("UM"))
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

    private ObjectMapper getObjectMapper() {
        YAMLFactory jf = new YAMLFactory();
        jf.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        jf.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        ObjectMapper mapper = new ObjectMapper(jf);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    private ChangelogElement createFileChangelogRepresentation(Path file, Collection<Path> files) {
        if (file.toString().contains("CD") || file.toString().contains("CM")) {
            return ChangelogElementInclude.builder()
                                          .include(ChangelogInclude.builder()
                                                                   .file(file.toString())
                                                                   .build())
                                          .build();
        }
        var changeset = createChangeSet(file, files);
        return ChangelogElementChangeSet.builder()
                                        .changeSet(changeset)
                                        .build();
    }

    private ChangeSet createChangeSet(Path file, Collection<Path> files) {
        var rollback = findRollbackPath(file, files);
        var builder = ChangeSet.builder();
        builder.author(author)
               .id(file.getFileName().toString().replace("DD", "CD").replace("DM", "CM").replace(".sql", ""))
               .change(ChangeSqlFile.builder()
                                    .sqlFile(ChangeFile.builder()
                                                       .path(file.toString())
                                                       .encoding("UTF-8")
                                                       .build())
                                    .build());
        if (rollback != null) {
            builder.rollback(ChangeSqlFile.builder()
                                          .sqlFile(ChangeFile.builder()
                                                             .path(rollback.toString())
                                                             .encoding("UTF-8")
                                                             .build())
                                          .build());
        }
        return builder.build();
    }

    @Nullable
    private Path findRollbackPath(Path file, Collection<Path> files) {
        Path rollbackCandidate = file.getParent().resolve(file.getFileName().toString().replace("DD", "UD").replace("DM", "UM"));
        if (files.contains(rollbackCandidate)) {
            return rollbackCandidate;
        } else {
            getLogger().warn("Can't find rollback file for {}", file);
            return null;
        }
    }

    private Path generateFilePath(Path taskDir) {
        return taskDir.resolve(taskName + ".yaml");
    }

    private void fillActivitiesFromConfig(HashMap<String, String> activities) {
        if (config == null) {
            throw new GradleException("Can't find ChangelogPluginExtension.");
        }
        activities.putAll(config.getFileMap());
    }
}
