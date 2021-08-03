package liquibase.dwh.changelog.model.lb;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Элемент списка изменений - включение другого списка или изменения
 */
@JsonDeserialize(as = ChangelogElementInclude.class)
public class ChangelogElementInclude implements ChangelogElement {
    /**
     * Описание включение другого файла
     */
    private ChangelogInclude include;

    public ChangelogElementInclude(ChangelogInclude include) {
        this.include = include;
    }

    public ChangelogElementInclude() {
    }

    public static ChangelogElementIncludeBuilder builder() {
        return new ChangelogElementIncludeBuilder();
    }

    public ChangelogInclude getInclude() {
        return this.include;
    }

    public void setInclude(ChangelogInclude include) {
        this.include = include;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ChangelogElementInclude)) {
            return false;
        }
        final ChangelogElementInclude other = (ChangelogElementInclude) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$include = this.getInclude();
        final Object other$include = other.getInclude();
        if (this$include == null ? other$include != null : !this$include.equals(other$include)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof ChangelogElementInclude;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $include = this.getInclude();
        result = result * PRIME + ($include == null ? 43 : $include.hashCode());
        return result;
    }

    public String toString() {
        return "ChangelogElementInclude(include=" + this.getInclude() + ")";
    }

    public static class ChangelogElementIncludeBuilder {
        private ChangelogInclude include;

        ChangelogElementIncludeBuilder() {
        }

        public ChangelogElementIncludeBuilder include(ChangelogInclude include) {
            this.include = include;
            return this;
        }

        public ChangelogElementInclude build() {
            return new ChangelogElementInclude(include);
        }

        public String toString() {
            return "ChangelogElementInclude.ChangelogElementIncludeBuilder(include=" + this.include + ")";
        }
    }
}
