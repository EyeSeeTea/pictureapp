package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Header {
    private long id;
    private String shortName;
    private String name;
    private Form form;
    private int index;

    public Header(long id, String shortName, String name,
            Form form, int index) {

        this.id = required(id, "id is required");
        this.shortName = required(shortName, "shortName is required");
        this.name = required(name, "name is required");
        this.form = required(form, "form is required");
        this.index = required(index, "index is required");
    }


    public static Builder newBuilder() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public Form getForm() {
        return form;
    }

    public int getIndex() {
        return index;
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

    @Override
    public String toString() {
        return "Header{" +
                "id=" + id +
                ", shortName='" + shortName + '\'' +
                ", name='" + name + '\'' +
                ", form=" + form +
                ", index=" + index +
                '}';
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
            return new Header(
                    this.id,
                    this.shortName,
                    this.name,
                    this.form,
                    this.index
            );
        }
    }
}
