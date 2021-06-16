package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Логичейский путь списка изменений
 */
@JsonDeserialize(as = ChangelogElementLogicalPath.class)
public class ChangelogElementLogicalPath implements ChangelogElement {
    /**
     * логический путь к файлу (любое значение, защищает от переноса и переименования)
     */
    private String logicalFilePath;

    public ChangelogElementLogicalPath(String logicalFilePath) {
        this.logicalFilePath = logicalFilePath;
    }

    public ChangelogElementLogicalPath() {
    }

    public static ChangelogElementLogicalPathBuilder builder() {
        return new ChangelogElementLogicalPathBuilder();
    }

    public String getLogicalFilePath() {
        return this.logicalFilePath;
    }

    public void setLogicalFilePath(String logicalFilePath) {
        this.logicalFilePath = logicalFilePath;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangelogElementLogicalPath)) {
            return false;
        }
        final ChangelogElementLogicalPath other = (ChangelogElementLogicalPath) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$logicalFilePath = this.getLogicalFilePath();
        final Object other$logicalFilePath = other.getLogicalFilePath();
        if (this$logicalFilePath == null ? other$logicalFilePath != null : !this$logicalFilePath.equals(other$logicalFilePath)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangelogElementLogicalPath;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $logicalFilePath = this.getLogicalFilePath();
        result = result * PRIME + ($logicalFilePath == null ? 43 : $logicalFilePath.hashCode());
        return result;
    }

    public String toString() {
        return "ChangelogElementLogicalPath(logicalFilePath=" + this.getLogicalFilePath() + ")";
    }

    public static class ChangelogElementLogicalPathBuilder {
        private String logicalFilePath;

        ChangelogElementLogicalPathBuilder() {
        }

        public ChangelogElementLogicalPathBuilder logicalFilePath(String logicalFilePath) {
            this.logicalFilePath = logicalFilePath;
            return this;
        }

        public ChangelogElementLogicalPath build() {
            return new ChangelogElementLogicalPath(logicalFilePath);
        }

        public String toString() {
            return "ChangelogElementLogicalPath.ChangelogElementLogicalPathBuilder(logicalFilePath=" + this.logicalFilePath + ")";
        }
    }
}
