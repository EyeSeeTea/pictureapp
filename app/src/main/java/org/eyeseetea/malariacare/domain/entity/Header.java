package org.eyeseetea.malariacare.domain.entity;


public class Header {
    private long id;
    private String shortName;
    private String name;
    private Form form;
    private int index;

    public Header(long id, String shortName, String name,
            Form form, int index) {
        this.id = id;
        this.shortName = shortName;
        this.name = name;
        this.form = form;
        this.index = index;
    }

    public Header() {
    }

    private Header(Builder builder) {
        setId(builder.id);
        setShortName(builder.shortName);
        setName(builder.name);
        setForm(builder.form);
        setIndex(builder.index);
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Header header = (Header) o;

        if (id != header.id) return false;
        if (index != header.index) return false;
        if (shortName != null ? !shortName.equals(header.shortName) : header.shortName != null) {
            return false;
        }
        if (name != null ? !name.equals(header.name) : header.name != null) return false;
        return form != null ? form.equals(header.form) : header.form == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (shortName != null ? shortName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (form != null ? form.hashCode() : 0);
        result = 31 * result + index;
        return result;
    }

    public static final class Builder {
        private long id;
        private String shortName;
        private String name;
        private Form form;
        private int index;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder shortName(String val) {
            shortName = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder form(Form val) {
            form = val;
            return this;
        }

        public Builder index(int val) {
            index = val;
            return this;
        }

        public Header build() {
            return new Header(this);
        }
    }
}
