package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Элемент файла набора изменений - изменение
 */

@JsonDeserialize(as = ChangelogElementChangeSet.class)
public class ChangelogElementChangeSet implements ChangelogElement {
    /**
     * Изменение
     */
    private ChangeSet changeSet;

    public ChangelogElementChangeSet(ChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    public ChangelogElementChangeSet() {
    }

    public static ChangelogElementChangeSetBuilder builder() {
        return new ChangelogElementChangeSetBuilder();
    }

    public ChangeSet getChangeSet() {
        return this.changeSet;
    }

    public void setChangeSet(ChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangelogElementChangeSet)) {
            return false;
        }
        final ChangelogElementChangeSet other = (ChangelogElementChangeSet) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$changeSet = this.getChangeSet();
        final Object other$changeSet = other.getChangeSet();
        if (this$changeSet == null ? other$changeSet != null : !this$changeSet.equals(other$changeSet)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangelogElementChangeSet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $changeSet = this.getChangeSet();
        result = result * PRIME + ($changeSet == null ? 43 : $changeSet.hashCode());
        return result;
    }

    public String toString() {
        return "ChangelogElementChangeSet(changeSet=" + this.getChangeSet() + ")";
    }

    public static class ChangelogElementChangeSetBuilder {
        private ChangeSet changeSet;

        ChangelogElementChangeSetBuilder() {
        }

        public ChangelogElementChangeSetBuilder changeSet(ChangeSet changeSet) {
            this.changeSet = changeSet;
            return this;
        }

        public ChangelogElementChangeSet build() {
            return new ChangelogElementChangeSet(changeSet);
        }

        public String toString() {
            return "ChangelogElementChangeSet.ChangelogElementChangeSetBuilder(changeSet=" + this.changeSet + ")";
        }
    }
}
