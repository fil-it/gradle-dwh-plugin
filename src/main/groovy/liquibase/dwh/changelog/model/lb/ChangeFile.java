package liquibase.dwh.changelog.model.lb;

/**
 * Путь к файлу с чейнжсетом
 */
public class ChangeFile {
    /**
     * кодировка файла
     */
    private String encoding;

    /**
     * путь к файлу
     */
    private String path;

    public ChangeFile(String encoding, String path) {
        this.encoding = encoding;
        this.path = path;
    }

    public ChangeFile() {
    }

    public static ChangeFileBuilder builder() {
        return new ChangeFileBuilder();
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getPath() {
        return this.path;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangeFile)) {
            return false;
        }
        final ChangeFile other = (ChangeFile) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$encoding = this.getEncoding();
        final Object other$encoding = other.getEncoding();
        if (this$encoding == null ? other$encoding != null : !this$encoding.equals(other$encoding)) {
            return false;
        }
        final Object this$path = this.getPath();
        final Object other$path = other.getPath();
        if (this$path == null ? other$path != null : !this$path.equals(other$path)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangeFile;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $encoding = this.getEncoding();
        result = result * PRIME + ($encoding == null ? 43 : $encoding.hashCode());
        final Object $path = this.getPath();
        result = result * PRIME + ($path == null ? 43 : $path.hashCode());
        return result;
    }

    public String toString() {
        return "ChangeFile(encoding=" + this.getEncoding() + ", path=" + this.getPath() + ")";
    }

    public static class ChangeFileBuilder {
        private String encoding;

        private String path;

        ChangeFileBuilder() {
        }

        public ChangeFileBuilder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public ChangeFileBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ChangeFile build() {
            return new ChangeFile(encoding, path);
        }

        public String toString() {
            return "ChangeFile.ChangeFileBuilder(encoding=" + this.encoding + ", path=" + this.path + ")";
        }
    }
}
