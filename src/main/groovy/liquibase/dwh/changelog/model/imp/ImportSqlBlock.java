package liquibase.dwh.changelog.model.imp;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * todo
 */
public class ImportSqlBlock {
    /**
     * todo
     */
    private List<String> header;

    /**
     * todo
     */
    private List<String> lines;

    public ImportSqlBlock(List<String> header, List<String> lines) {
        this.header = header;
        this.lines = lines;
    }

    public ImportSqlBlock() {
    }

    public static ImportSqlBlockBuilder builder() {
        return new ImportSqlBlockBuilder();
    }

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

    public List<String> header() {
        return this.header;
    }

    public List<String> lines() {
        return this.lines;
    }

    public ImportSqlBlock header(List<String> header) {
        this.header = header;
        return this;
    }

    public ImportSqlBlock lines(List<String> lines) {
        this.lines = lines;
        return this;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ImportSqlBlock)) {
            return false;
        }
        final ImportSqlBlock other = (ImportSqlBlock) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$header = this.header();
        final Object other$header = other.header();
        if (this$header == null ? other$header != null : !this$header.equals(other$header)) {
            return false;
        }
        final Object this$lines = this.lines();
        final Object other$lines = other.lines();
        if (this$lines == null ? other$lines != null : !this$lines.equals(other$lines)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ImportSqlBlock;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $header = this.header();
        result = result * PRIME + ($header == null ? 43 : $header.hashCode());
        final Object $lines = this.lines();
        result = result * PRIME + ($lines == null ? 43 : $lines.hashCode());
        return result;
    }

    public String toString() {
        return "ImportSqlBlock(header=" + this.header() + ", lines=" + this.lines() + ")";
    }

    public static class ImportSqlBlockBuilder {
        private ArrayList<String> header;

        private ArrayList<String> lines;

        ImportSqlBlockBuilder() {
        }

        public ImportSqlBlockBuilder header(String header) {
            if (this.header == null) {
                this.header = new ArrayList<String>();
            }
            this.header.add(header);
            return this;
        }

        public ImportSqlBlockBuilder header(Collection<? extends String> header) {
            if (this.header == null) {
                this.header = new ArrayList<String>();
            }
            this.header.addAll(header);
            return this;
        }

        public ImportSqlBlockBuilder clearHeader() {
            if (this.header != null) {
                this.header.clear();
            }
            return this;
        }

        public ImportSqlBlockBuilder line(String line) {
            if (this.lines == null) {
                this.lines = new ArrayList<String>();
            }
            this.lines.add(line);
            return this;
        }

        public ImportSqlBlockBuilder lines(Collection<? extends String> lines) {
            if (this.lines == null) {
                this.lines = new ArrayList<String>();
            }
            this.lines.addAll(lines);
            return this;
        }

        public ImportSqlBlockBuilder clearLines() {
            if (this.lines != null) {
                this.lines.clear();
            }
            return this;
        }

        public ImportSqlBlock build() {
            List<String> header;
            switch (this.header == null ? 0 : this.header.size()) {
                case 0:
                    header = java.util.Collections.emptyList();
                    break;
                case 1:
                    header = java.util.Collections.singletonList(this.header.get(0));
                    break;
                default:
                    header = java.util.Collections.unmodifiableList(new ArrayList<String>(this.header));
            }
            List<String> lines;
            switch (this.lines == null ? 0 : this.lines.size()) {
                case 0:
                    lines = java.util.Collections.emptyList();
                    break;
                case 1:
                    lines = java.util.Collections.singletonList(this.lines.get(0));
                    break;
                default:
                    lines = java.util.Collections.unmodifiableList(new ArrayList<String>(this.lines));
            }

            return new ImportSqlBlock(header, lines);
        }

        public String toString() {
            return "ImportSqlBlock.ImportSqlBlockBuilder(header=" + this.header + ", lines=" + this.lines + ")";
        }
    }
}
