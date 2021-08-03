package liquibase.dwh.changelog.model.lb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Описание изменения
 */
public class ChangeSet {
    /**
     * идентификатор изменения
     */
    private String id;

    /**
     * имя автора изменения
     */
    private String author;

    /**
     * список изменений
     */
    private List<? extends Change> changes;

    /**
     * список изменений, описывающих откат
     */
    private List<? extends Change> rollback;

    public ChangeSet(String id, String author, List<? extends Change> changes, List<? extends Change> rollback) {
        this.id = id;
        this.author = author;
        this.changes = changes;
        this.rollback = rollback;
    }

    public ChangeSet() {
    }

    public static ChangeSetBuilder builder() {
        return new ChangeSetBuilder();
    }

    public String getId() {
        return this.id;
    }

    public String getAuthor() {
        return this.author;
    }

    public List<? extends Change> getChanges() {
        return this.changes;
    }

    public List<? extends Change> getRollback() {
        return this.rollback;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setChanges(List<? extends Change> changes) {
        this.changes = changes;
    }

    public void setRollback(List<? extends Change> rollback) {
        this.rollback = rollback;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangeSet)) {
            return false;
        }
        final ChangeSet other = (ChangeSet) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) {
            return false;
        }
        final Object this$author = this.getAuthor();
        final Object other$author = other.getAuthor();
        if (this$author == null ? other$author != null : !this$author.equals(other$author)) {
            return false;
        }
        final Object this$changes = this.getChanges();
        final Object other$changes = other.getChanges();
        if (this$changes == null ? other$changes != null : !this$changes.equals(other$changes)) {
            return false;
        }
        final Object this$rollback = this.getRollback();
        final Object other$rollback = other.getRollback();
        if (this$rollback == null ? other$rollback != null : !this$rollback.equals(other$rollback)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangeSet;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $author = this.getAuthor();
        result = result * PRIME + ($author == null ? 43 : $author.hashCode());
        final Object $changes = this.getChanges();
        result = result * PRIME + ($changes == null ? 43 : $changes.hashCode());
        final Object $rollback = this.getRollback();
        result = result * PRIME + ($rollback == null ? 43 : $rollback.hashCode());
        return result;
    }

    public String toString() {
        return "ChangeSet(id=" + this.getId() + ", author=" + this.getAuthor() + ", changes=" + this.getChanges() + ", rollback=" + this.getRollback() + ")";
    }

    public static class ChangeSetBuilder {
        private String id;

        private String author;

        private ArrayList<Change> changes;

        private ArrayList<Change> rollback;

        ChangeSetBuilder() {
        }

        public ChangeSetBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ChangeSetBuilder author(String author) {
            this.author = author;
            return this;
        }

        public ChangeSetBuilder change(Change change) {
            if (this.changes == null) {
                this.changes = new ArrayList<Change>();
            }
            this.changes.add(change);
            return this;
        }

        public ChangeSetBuilder changes(Collection<? extends Change> changes) {
            if (this.changes == null) {
                this.changes = new ArrayList<Change>();
            }
            this.changes.addAll(changes);
            return this;
        }

        public ChangeSetBuilder clearChanges() {
            if (this.changes != null) {
                this.changes.clear();
            }
            return this;
        }

        public ChangeSetBuilder rollback(Change rollback) {
            if (this.rollback == null) {
                this.rollback = new ArrayList<Change>();
            }
            this.rollback.add(rollback);
            return this;
        }

        public ChangeSetBuilder rollback(Collection<? extends Change> rollback) {
            if (this.rollback == null) {
                this.rollback = new ArrayList<Change>();
            }
            this.rollback.addAll(rollback);
            return this;
        }

        public ChangeSetBuilder clearRollback() {
            if (this.rollback != null) {
                this.rollback.clear();
            }
            return this;
        }

        public ChangeSet build() {
            List<Change> changes;
            switch (this.changes == null ? 0 : this.changes.size()) {
                case 0:
                    changes = java.util.Collections.emptyList();
                    break;
                case 1:
                    changes = java.util.Collections.singletonList(this.changes.get(0));
                    break;
                default:
                    changes = java.util.Collections.unmodifiableList(new ArrayList<Change>(this.changes));
            }
            List<Change> rollback;
            switch (this.rollback == null ? 0 : this.rollback.size()) {
                case 0:
                    rollback = java.util.Collections.emptyList();
                    break;
                case 1:
                    rollback = java.util.Collections.singletonList(this.rollback.get(0));
                    break;
                default:
                    rollback = java.util.Collections.unmodifiableList(new ArrayList<Change>(this.rollback));
            }

            return new ChangeSet(id, author, changes, rollback);
        }

        public String toString() {
            return "ChangeSet.ChangeSetBuilder(id=" + this.id + ", author=" + this.author + ", changes=" + this.changes + ", rollback=" + this.rollback + ")";
        }
    }
}
