package liquibase.dwh.changelog.model.lb;

/**
 * Включение файла в списка изменений
 */
public class ChangelogInclude {
    /**
     * путь к файлу
     */
    private String file;

    /**
     * флаг относительный путь или нет
     */
    private Boolean relativeToChangelogFile;

    public ChangelogInclude(String file, Boolean relativeToChangelogFile) {
        this.file = file;
        this.relativeToChangelogFile = relativeToChangelogFile;
    }

    public ChangelogInclude() {
    }

    public static ChangelogIncludeBuilder builder() {
        return new ChangelogIncludeBuilder();
    }

    public String getFile() {
        return this.file;
    }

    public Boolean getRelativeToChangelogFile() {
        return this.relativeToChangelogFile;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setRelativeToChangelogFile(Boolean relativeToChangelogFile) {
        this.relativeToChangelogFile = relativeToChangelogFile;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangelogInclude)) {
            return false;
        }
        final ChangelogInclude other = (ChangelogInclude) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$file = this.getFile();
        final Object other$file = other.getFile();
        if (this$file == null ? other$file != null : !this$file.equals(other$file)) {
            return false;
        }
        final Object this$relativeToChangelogFile = this.getRelativeToChangelogFile();
        final Object other$relativeToChangelogFile = other.getRelativeToChangelogFile();
        if (this$relativeToChangelogFile == null ?
                other$relativeToChangelogFile != null :
                !this$relativeToChangelogFile.equals(other$relativeToChangelogFile)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangelogInclude;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $file = this.getFile();
        result = result * PRIME + ($file == null ? 43 : $file.hashCode());
        final Object $relativeToChangelogFile = this.getRelativeToChangelogFile();
        result = result * PRIME + ($relativeToChangelogFile == null ? 43 : $relativeToChangelogFile.hashCode());
        return result;
    }

    public String toString() {
        return "ChangelogInclude(file=" + this.getFile() + ", relativeToChangelogFile=" + this.getRelativeToChangelogFile() + ")";
    }

    public static class ChangelogIncludeBuilder {
        private String file;

        private Boolean relativeToChangelogFile;

        ChangelogIncludeBuilder() {
        }

        public ChangelogIncludeBuilder file(String file) {
            this.file = file;
            return this;
        }

        public ChangelogIncludeBuilder relativeToChangelogFile(Boolean relativeToChangelogFile) {
            this.relativeToChangelogFile = relativeToChangelogFile;
            return this;
        }

        public ChangelogInclude build() {
            return new ChangelogInclude(file, relativeToChangelogFile);
        }

        public String toString() {
            return "ChangelogInclude.ChangelogIncludeBuilder(file=" + this.file + ", relativeToChangelogFile=" + this.relativeToChangelogFile + ")";
        }
    }
}
