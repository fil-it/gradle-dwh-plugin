package liquibase.dwh.changelog.model.imp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import org.gradle.api.GradleException;


/**
 * SQL выражения собранные для записи в один файл
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Singular
    private List<ImportSqlBlock> blocks;

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

}
