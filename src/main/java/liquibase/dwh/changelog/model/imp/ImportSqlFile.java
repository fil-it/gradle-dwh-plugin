package liquibase.dwh.changelog.model.imp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * todo
 */
@Data
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportSqlFile {
    /**
     * todo
     */
    private String filePath;

    /**
     * todo
     */
    private String fileName;

    /**
     * todo
     */
    @Singular
    private List<ImportSqlBlock> blocks;

    /**
     * todo
     *
     * @param scheme todo
     * @throws GradleException todo
     */
    public void writeFile(File scheme) throws GradleException {
        if (filePath == null || fileName == null) {
            throw new GradleException("Cannot write block: \n" + this);
        }
        Path destinationFolder = scheme.toPath().resolve(filePath);
        destinationFolder.toFile().mkdirs();
        final Path yamlFile = destinationFolder.resolve(fileName);
        if (yamlFile.toFile().exists()) {
            throw new GradleException("File '" + yamlFile.toFile().getName() + "' suddenly exists. Skipping.");
        }
        try (PrintWriter fileWriter = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(yamlFile.toFile()), StandardCharsets.UTF_8
        ))) {
            blocks.forEach(block -> block.write(fileWriter));
        } catch (IOException e) {
            throw new GradleException("Cannot write file " + yamlFile, e);
        }
    }

}
