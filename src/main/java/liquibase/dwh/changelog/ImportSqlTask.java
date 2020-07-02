package liquibase.dwh.changelog;

import liquibase.dwh.changelog.configuration.ChangelogPluginExtension;
import liquibase.dwh.changelog.model.imp.ImportSqlBlock;
import liquibase.dwh.changelog.model.imp.ImportSqlFile;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * todo
 */
public class ImportSqlTask extends DefaultTask {
    /**
     * todo
     */
    private ChangelogPluginExtension config;

    /**
     * todo
     */
    private String fileString;

    /**
     * todo
     */
    private String scheme;

    /**
     * todo
     */
    private String taskName;

    /**
     * todo
     */
    private String author;

    {
        setGroup("DWH");
    }

    /**
     * todo
     *
     * @param scheme todo
     */
    @Option(option = "scheme", description = "Configures the scheme path importing to.")
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * todo
     *
     * @param file todo
     */
    @Option(option = "file", description = "Configures the file to be imported.")
    public void setFile(String file) {
        this.fileString = file;
    }

    /**
     * Точка входа в задачу
     */
    @TaskAction
    public void action() {
        if (fileString == null) {
            throw new GradleException("Parameter --file not specified");
        }
        final File file = new File(this.fileString);
        if (!file.isFile()) {
            throw new GradleException("File '" + file.getAbsolutePath() + "' not found");
        }
        this.taskName = GitMetaProvider.extractTaskName(getProject().getRootDir(), getLogger());
        this.author = GitMetaProvider.extractAuthor(getProject().getRootDir(), getLogger());
        this.config = getProject().getExtensions().findByType(ChangelogPluginExtension.class);
        List<String> allFile = readFile(file);
        List<ImportSqlBlock> blocks = sliceFile(allFile);
        final File schemeFile = getProject().file(scheme);
        if (!schemeFile.getParentFile().getName().equals("schemas") || !schemeFile.getParentFile().exists()) {
            throw new GradleException("Invalid schema folder path, parent `schemas` not found");
        }
        schemeFile.mkdirs();
        List<ImportSqlFile> files = sliceBlocks(blocks);
        for (ImportSqlFile importSqlFile : files) {
            try {
                importSqlFile.writeFile(schemeFile);
            } catch (GradleException e) {
                getLogger().quiet(e.getMessage());
            }
        }
        System.out.println("blocks = " + blocks);
    }

    /**
     * todo
     *
     * @param blocks todo
     * @return todo
     */
    private List<ImportSqlFile> sliceBlocks(List<ImportSqlBlock> blocks) {
        final ArrayList<ImportSqlFile> result = new ArrayList<>();
        ImportSqlFile.ImportSqlFileBuilder currentFile = null;

        for (ImportSqlBlock block : blocks) {
            if (block.headerContains("Created on") || block.headerContains("Table")) {
                if (currentFile != null) {
                    result.add(currentFile.build());
                }
                currentFile = ImportSqlFile.builder();
                currentFile.fileName("1-0-0-CD-" + taskName + "-init.sql");
                if (block.headerContains("Created on")) {
                    currentFile.filePath("");
                } else {
                    currentFile.filePath("tables/" + block.headerGetValue("Table"));
                }
                currentFile.block(block);
            } else {
                if (currentFile == null) {
                    throw new GradleException("First sql block is invalid!");
                }
                currentFile.block(block);
            }
        }
        return result;
    }

    /**
     * todo
     *
     * @param file todo
     * @return todo
     */
    private List<String> readFile(File file) {
        try (LineNumberReader lineReader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            return lineReader.lines().collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new GradleException("Cannot read file", e);
        }
    }

    /**
     * todo
     *
     * @param allFile todo
     * @return todo
     */
    private List<ImportSqlBlock> sliceFile(List<String> allFile) {
        final ArrayList<ImportSqlBlock> blocks = new ArrayList<>();
        boolean inHeader = false;
        ImportSqlBlock.ImportSqlBlockBuilder currentBlock = null;
        try {
            for (String s : allFile) {
                if (!s.isEmpty()) {
                    if (s.equals("/*==============================================================*/")) {
                        inHeader = !inHeader;
                        if (inHeader) {
                            if (currentBlock != null) {
                                blocks.add(currentBlock.build());
                            }
                            currentBlock = ImportSqlBlock.builder();
                        }
                    } else if (inHeader) {
                        currentBlock.header(s);
                    } else {
                        currentBlock.line(s);
                    }
                }
            }
        } catch (NullPointerException e) {
            if (currentBlock == null) {
                throw new GradleException("File starts with non-header string", e);
            } else {
                throw e;
            }
        }
        if (currentBlock == null) {
            throw new GradleException("Empty file!");
        }
        blocks.add(currentBlock.build());
        return blocks;
    }
}
