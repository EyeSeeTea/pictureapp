package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Country {
    private long id;
    private String uid;
    private String name;

    public Country(long id, String uid, String name) {
        this.id = required(id, "id is required");
        this.uid = required(uid, "uid is required");
        this.name = name;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public long getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
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
