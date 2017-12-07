package org.eyeseetea.malariacare.domain.entity;



import java.util.List;

public class Question {
    private long id;
    private String code;
    private String name;
    private Type type;
    private boolean compulsory;
    private List<Option> options;
    private Header header;
    private int index;
    private Visibility visibility;


    public Question(String code, String name,
            Type type, boolean compulsory,
            List<Option> options, Header header, int index) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.compulsory = compulsory;
        this.options = options;
        this.header = header;
        this.index = index;
    }

    public Question() {
    }

    private Question(Builder builder) {
        setId(builder.id);
        setCode(builder.code);
        setName(builder.name);
        setType(builder.type);
        setCompulsory(builder.compulsory);
        setOptions(builder.options);
        setHeader(builder.header);
        setIndex(builder.index);
        setVisibility(builder.visibility);
    }

    public Question(long id, String code, String name,
            Type type, boolean compulsory,
            List<Option> options, Header header, int index) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.compulsory = compulsory;
        this.options = options;
        this.header = header;
        this.index = index;
    }

    public Question(long id, String code, String name,
            Type type, boolean compulsory,
            List<Option> options, Header header, int index,
            Visibility visibility) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.type = type;
        this.compulsory = compulsory;
        this.options = options;
        this.header = header;
        this.index = index;
        this.visibility = visibility;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public List<Option> getOptions() {
        return options;
    }
    public boolean hasOptions(){
        return options !=null;
    }
    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", compulsory=" + compulsory +
                ", options=" + options +
                ", header=" + header +
                ", index=" + index +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (id != question.id) return false;
        if (compulsory != question.compulsory) return false;
        if (index != question.index) return false;
        if (code != null ? !code.equals(question.code) : question.code != null) return false;
        if (name != null ? !name.equals(question.name) : question.name != null) return false;
        if (type != question.type) return false;
        if (options != null ? !options.equals(question.options) : question.options != null) {
            return false;
        }
        return header != null ? header.equals(question.header) : question.header == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (compulsory ? 1 : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + index;
        return result;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }


    public enum Type {
        SHORT_TEXT, PHONE, DROPDOWN_LIST, YEAR, DATE, INT, LONG_TEXT, POSITIVE_INT,
        PREGNANT_MONTH, RADIO_GROUP_HORIZONTAL, QUESTION_LABEL, SWITCH_BUTTON
    }


    public static final class Builder {
        private String code;
        private String name;
        private Type type;
        private boolean compulsory;
        private List<Option> options;
        private Header header;
        private int index;
        private Visibility visibility;
        private long id;

        public Builder() {
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder type(Type val) {
            type = val;
            return this;
        }

        public Builder compulsory(boolean val) {
            compulsory = val;
            return this;
        }

        public Builder options(List<Option> val) {
            options = val;
            return this;
        }

        public Builder header(Header val) {
            header = val;
            return this;
        }

        public Builder index(int val) {
            index = val;
            return this;
        }

        public Builder visibility(Visibility val) {
            visibility = val;
            return this;
        }

        public Question build() {
            return new Question(this);
        }

        public Builder id(long val) {
            id = val;
            return this;
        }
    }

    public enum  Visibility{
        VISIBLE,INVISIBLE,IMPORTANT
    }
}
