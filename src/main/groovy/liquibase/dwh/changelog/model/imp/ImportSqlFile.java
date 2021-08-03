package liquibase.dwh.changelog.model.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gradle.api.GradleException;


/**
 * SQL выражения собранные для записи в один файл
 */
public class ImportSqlFile {
    /**
     * путь до целевого каталога
     */
    private String filePath;

    /**
     * имя файла
     */
    private String fileName;

    /**
     * автор изменений
     */
    private String author;

    /**
     * имя текущего таска в jira
     */
    private String taskName;

    /**
     * Список логических блоков sql выражений
     */
    private List<ImportSqlBlock> blocks;

    public ImportSqlFile(String filePath, String fileName, String author, String taskName, List<ImportSqlBlock> blocks) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.author = author;
        this.taskName = taskName;
        this.blocks = blocks;
    }

    public ImportSqlFile() {
    }

    public static ImportSqlFileBuilder builder() {
        return new ImportSqlFileBuilder();
    }

    /**
     * записывает данные в файл
     *
     * @param scheme целевой файл
     */
    public void writeFile(File scheme) {
        if (filePath == null || fileName == null) {
            throw new GradleException("Cannot write block: \n" + this);
        }
        Path destinationFolder = scheme.toPath().resolve(filePath);
        destinationFolder.toFile().mkdirs();
        final Path yamlFile = destinationFolder.resolve(fileName);
        if (yamlFile.toFile().exists()) {
            throw new GradleException("File '" + yamlFile.toFile().getAbsolutePath() + "' suddenly exists. Skipping.");
        }
        try (PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(yamlFile.toFile()), StandardCharsets.UTF_8
        ))) {
            fileWriter.println("--liquibase formatted sql");
            fileWriter.println(String.format("--changeset %s:%s runInTransaction:true", author, fileName));
            blocks.forEach(block -> block.write(fileWriter));
        } catch (IOException e) {
            throw new GradleException("Cannot write file " + yamlFile, e);
        }
    }

    public String filePath() {
        return this.filePath;
    }

    public String fileName() {
        return this.fileName;
    }

    public String author() {
        return this.author;
    }

    public String taskName() {
        return this.taskName;
    }

    public List<ImportSqlBlock> blocks() {
        return this.blocks;
    }

    public ImportSqlFile filePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public ImportSqlFile fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImportSqlFile author(String author) {
        this.author = author;
        return this;
    }

    public ImportSqlFile taskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public ImportSqlFile blocks(List<ImportSqlBlock> blocks) {
        this.blocks = blocks;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ImportSqlFile)) {
            return false;
        }
        final ImportSqlFile other = (ImportSqlFile) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$filePath = this.filePath();
        final Object other$filePath = other.filePath();
        if (this$filePath == null ? other$filePath != null : !this$filePath.equals(other$filePath)) {
            return false;
        }
        final Object this$fileName = this.fileName();
        final Object other$fileName = other.fileName();
        if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
            return false;
        }
        final Object this$author = this.author();
        final Object other$author = other.author();
        if (this$author == null ? other$author != null : !this$author.equals(other$author)) {
            return false;
        }
        final Object this$taskName = this.taskName();
        final Object other$taskName = other.taskName();
        if (this$taskName == null ? other$taskName != null : !this$taskName.equals(other$taskName)) {
            return false;
        }
        final Object this$blocks = this.blocks();
        final Object other$blocks = other.blocks();
        if (this$blocks == null ? other$blocks != null : !this$blocks.equals(other$blocks)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ImportSqlFile;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $filePath = this.filePath();
        result = result * PRIME + ($filePath == null ? 43 : $filePath.hashCode());
        final Object $fileName = this.fileName();
        result = result * PRIME + ($fileName == null ? 43 : $fileName.hashCode());
        final Object $author = this.author();
        result = result * PRIME + ($author == null ? 43 : $author.hashCode());
        final Object $taskName = this.taskName();
        result = result * PRIME + ($taskName == null ? 43 : $taskName.hashCode());
        final Object $blocks = this.blocks();
        result = result * PRIME + ($blocks == null ? 43 : $blocks.hashCode());
        return result;
    }

    public String toString() {
        return "ImportSqlFile(filePath=" + this.filePath() + ", fileName=" + this.fileName() + ", author=" + this.author() + ", taskName=" + this.taskName() + ", blocks=" + this
                .blocks() + ")";
    }

    public static class ImportSqlFileBuilder {
        private String filePath;

        private String fileName;

        private String author;

        private String taskName;

        private ArrayList<ImportSqlBlock> blocks;

        ImportSqlFileBuilder() {
        }

        public ImportSqlFileBuilder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public ImportSqlFileBuilder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public ImportSqlFileBuilder author(String author) {
            this.author = author;
            return this;
        }

        public ImportSqlFileBuilder taskName(String taskName) {
            this.taskName = taskName;
            return this;
        }

        public ImportSqlFileBuilder block(ImportSqlBlock block) {
            if (this.blocks == null) {
                this.blocks = new ArrayList<ImportSqlBlock>();
            }
            this.blocks.add(block);
            return this;
        }

        public ImportSqlFileBuilder blocks(Collection<? extends ImportSqlBlock> blocks) {
            if (this.blocks == null) {
                this.blocks = new ArrayList<ImportSqlBlock>();
            }
            this.blocks.addAll(blocks);
            return this;
        }

        public ImportSqlFileBuilder clearBlocks() {
            if (this.blocks != null) {
                this.blocks.clear();
            }
            return this;
        }

        public ImportSqlFile build() {
            List<ImportSqlBlock> blocks;
            switch (this.blocks == null ? 0 : this.blocks.size()) {
                case 0:
                    blocks = java.util.Collections.emptyList();
                    break;
                case 1:
                    blocks = java.util.Collections.singletonList(this.blocks.get(0));
                    break;
                default:
                    blocks = java.util.Collections.unmodifiableList(new ArrayList<ImportSqlBlock>(this.blocks));
            }

            return new ImportSqlFile(filePath, fileName, author, taskName, blocks);
        }

        public String toString() {
            return "ImportSqlFile.ImportSqlFileBuilder(filePath=" + this.filePath + ", fileName=" + this.fileName + ", author=" + this.author + ", taskName=" + this.taskName + ", blocks=" + this.blocks + ")";
        }
    }
}
