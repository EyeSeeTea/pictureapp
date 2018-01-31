package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Form {
    private long id;
    private String name;
    private int index;
    private Type type;
    private Country country;

    public Form(long id, String name, int index, Type type,
            Country country) {

        setId(required(id, "id is required"));
        setName(required(name, "name is required"));
        setIndex(required(index, "index is required"));
        setType(required(type, "type is required"));
        setCountry(required(country, "country is required"));
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

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    private void setIndex(int index) {
        this.index = index;
    }

    public Type getType() {
        return type;
    }

    private void setType(Type type) {
        this.type = type;
    }

    public Country getCountry() {
        return country;
    }

    private void setCountry(Country country) {
        this.country = country;
    }

    public enum Type {
        AUTOMATIC, AUTOMATIC_NON_SCORED, COMPOSITE_SCORE, SCORE_SUMMARY,
        ADHERENCE, IQA, REPORTING, DYNAMIC_AUTOMATIC, MULTI_QUESTION,
        DYNAMIC_TREATMENT, MULTI_QUESTION_EXCLUSIVE

    }

    public static final class Builder {
        private long id;
        private String name;
        private int index;
        private Type type;
        private Country country;

        public Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder index(int val) {
            index = val;
            return this;
        }

        public Builder type(Type val) {
            type = val;
            return this;
        }

        public Builder country(Country val) {
            country = val;
            return this;
        }

        public Form build() {
            return new Form(
                    this.id,
                    this.name,
                    this.index,
                    this.type,
                    this.country
            );
        }
    }
}
