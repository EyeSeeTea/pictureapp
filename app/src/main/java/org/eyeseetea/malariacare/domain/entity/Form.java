package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Form {
    private long id;
    private String name;
    private int index;
    private Type type;
    private Country country;

    private Form(Builder builder) {
        setId(required(builder.id,"id is required"));
        setName(required(builder.name,"name is required"));
        setIndex(required(builder.index,"index is required"));
        setType(required(builder.type,"type is required"));
        setCountry(required(builder.country,"country is required"));
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
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
            return new Form(this);
        }
    }
}
