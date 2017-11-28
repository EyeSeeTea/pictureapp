package org.eyeseetea.malariacare.domain.entity;


public class Option {
    private String code;
    private String name;


    public Option(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Option() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (!code.equals(option.code)) return false;
        return name.equals(option.name);
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Option{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
