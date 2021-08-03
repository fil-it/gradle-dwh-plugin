package liquibase.dwh.changelog.model.lb;

/**
 * Конкретное изменение в виде sql-файла
 */
public class ChangeSqlFile implements Change {
    /**
     * Изменение в sql-файле
     */
    private ChangeFile sqlFile;

    public ChangeSqlFile(ChangeFile sqlFile) {
        this.sqlFile = sqlFile;
    }

    public ChangeSqlFile() {
    }

    public static ChangeSqlFileBuilder builder() {
        return new ChangeSqlFileBuilder();
    }

    public ChangeFile getSqlFile() {
        return this.sqlFile;
    }

    public void setSqlFile(ChangeFile sqlFile) {
        this.sqlFile = sqlFile;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangeSqlFile)) {
            return false;
        }
        final ChangeSqlFile other = (ChangeSqlFile) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$sqlFile = this.getSqlFile();
        final Object other$sqlFile = other.getSqlFile();
        if (this$sqlFile == null ? other$sqlFile != null : !this$sqlFile.equals(other$sqlFile)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangeSqlFile;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $sqlFile = this.getSqlFile();
        result = result * PRIME + ($sqlFile == null ? 43 : $sqlFile.hashCode());
        return result;
    }

    public String toString() {
        return "ChangeSqlFile(sqlFile=" + this.getSqlFile() + ")";
    }

    public static class ChangeSqlFileBuilder {
        private ChangeFile sqlFile;

        ChangeSqlFileBuilder() {
        }

        public ChangeSqlFileBuilder sqlFile(ChangeFile sqlFile) {
            this.sqlFile = sqlFile;
            return this;
        }

        public ChangeSqlFile build() {
            return new ChangeSqlFile(sqlFile);
        }

        public String toString() {
            return "ChangeSqlFile.ChangeSqlFileBuilder(sqlFile=" + this.sqlFile + ")";
        }
    }
}
