package org.eyeseetea.malariacare.domain.entity;


public class Country {
    private long id;
    private String uid;
    private String name;

    public Country(long id, String uid, String name) {
        this.id = id;
        this.uid = uid;
        this.name = name;
    }

    public Country() {
    }

    private Country(Builder builder) {
        setId(builder.id);
        setUid(builder.uid);
        setName(builder.name);
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (id != country.id) return false;
        if (uid != null ? !uid.equals(country.uid) : country.uid != null) return false;
        return name != null ? name.equals(country.name) : country.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private long id;
        private String uid;
        private String name;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder uid(String val) {
            uid = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Country build() {
            return new Country(this);
        }
    }
}
