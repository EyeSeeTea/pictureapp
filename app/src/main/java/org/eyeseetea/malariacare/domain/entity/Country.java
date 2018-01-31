package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Country {
    private long id;
    private String uid;
    private String name;

    public Country(long id, String uid, String name) {
        setId(required(id, "id is required"));
        setUid(required(uid, "uid is required"));
        setName(name);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
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
            return new Country(
                    this.id,
                    this.uid,
                    this.name
            );
        }
    }
}
