package liquibase.dwh.changelog.model.imp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.experimental.Accessors;

import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

/**
 * todo
 */
@Data
@Accessors(fluent = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportSqlBlock {
    /**
     * todo
     */
    @Singular("header")
    private List<String> header;

    /**
     * todo
     */
    @Singular
    private List<String> lines;

    /**
     * todo
     *
     * @param string todo
     * @return todo
     */
    public boolean headerContains(String string) {
        return header.stream().anyMatch(s -> s.contains(string));
    }

    /**
     * todo
     *
     * @param string todo
     * @return todo
     */
    public String headerGetValue(String string) {
        final Optional<String> line = header.stream().filter(s -> s.contains(string)).findAny();
        if (!line.isPresent()) {
            return null;
        }
        final String[] split = line.get().replace("/*", "").replace("*/", "").trim().split(":", 2);
        return split[1].trim();
    }

    /**
     * todo
     *
     * @param fileWriter todo
     */
    public void write(PrintWriter fileWriter) {
        header.forEach(fileWriter::println);
        fileWriter.println();
        lines.forEach(fileWriter::println);
        fileWriter.println();
    }
}
