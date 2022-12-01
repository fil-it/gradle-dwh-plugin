package liquibase.dwh.changelog.model.lb;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Файл списка изменений
 */
public class ChangelogFile {

    /**
     * Элементы файла
     */
    private Collection<? extends ChangelogElement> databaseChangeLog;

    public ChangelogFile(Collection<? extends ChangelogElement> databaseChangeLog) {
        this.databaseChangeLog = databaseChangeLog;
    }

    public ChangelogFile() {
    }

    public static ChangelogFileBuilder builder() {
        return new ChangelogFileBuilder();
    }

    /**
     * Получить билдер из объекта
     * @return билдер
     */
    public ChangelogFileBuilder toBuilder() {
        return new ChangelogFileBuilder().databaseChangeLog(this.databaseChangeLog == null ? java.util.Collections.emptyList() : this.databaseChangeLog);
    }

    public Collection<? extends ChangelogElement> getDatabaseChangeLog() {
        return this.databaseChangeLog;
    }

    public void setDatabaseChangeLog(Collection<? extends ChangelogElement> databaseChangeLog) {
        this.databaseChangeLog = databaseChangeLog;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof ChangelogFile)) return false;
        final ChangelogFile other = (ChangelogFile) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$databaseChangeLog = this.getDatabaseChangeLog();
        final Object other$databaseChangeLog = other.getDatabaseChangeLog();
        if (this$databaseChangeLog == null ? other$databaseChangeLog != null : !this$databaseChangeLog.equals(other$databaseChangeLog))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangelogFile;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $databaseChangeLog = this.getDatabaseChangeLog();
        result = result * PRIME + ($databaseChangeLog == null ? 43 : $databaseChangeLog.hashCode());
        return result;
    }

    public String toString() {
        return "ChangelogFile(databaseChangeLog=" + this.getDatabaseChangeLog() + ")";
    }

    public static class ChangelogFileBuilder {
        private ArrayList<ChangelogElement> databaseChangeLog;

        ChangelogFileBuilder() {
        }

        public ChangelogFileBuilder element(ChangelogElement element) {
            if (this.databaseChangeLog == null) this.databaseChangeLog = new ArrayList<ChangelogElement>();
            this.databaseChangeLog.add(element);
            return this;
        }

        public ChangelogFileBuilder databaseChangeLog(Collection<? extends ChangelogElement> databaseChangeLog) {
            if (this.databaseChangeLog == null) this.databaseChangeLog = new ArrayList<ChangelogElement>();
            this.databaseChangeLog.addAll(databaseChangeLog);
            return this;
        }

        public ChangelogFileBuilder clearDatabaseChangeLog() {
            if (this.databaseChangeLog != null)
                this.databaseChangeLog.clear();
            return this;
        }

        public ChangelogFile build() {
            Collection<ChangelogElement> databaseChangeLog;
            switch (this.databaseChangeLog == null ? 0 : this.databaseChangeLog.size()) {
                case 0:
                    databaseChangeLog = java.util.Collections.emptyList();
                    break;
                case 1:
                    databaseChangeLog = java.util.Collections.singletonList(this.databaseChangeLog.get(0));
                    break;
                default:
                    databaseChangeLog = java.util.Collections.unmodifiableList(new ArrayList<ChangelogElement>(this.databaseChangeLog));
            }

            return new ChangelogFile(databaseChangeLog);
        }

        public String toString() {
            return "ChangelogFile.ChangelogFileBuilder(databaseChangeLog=" + this.databaseChangeLog + ")";
        }
    }
}


