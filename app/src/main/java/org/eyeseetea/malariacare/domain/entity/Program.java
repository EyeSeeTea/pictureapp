package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Program {
    private String code;
    private String id;

    public Program() {
    }

    public Program(String code, String id) {
        this.id = required(id,"ID is required");
        this.code = required(code,"Code is required");
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Program program = (Program) o;

        if (code != null ? !code.equals(program.code) : program.code != null) return false;
        return id != null ? id.equals(program.id) : program.id == null;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Program{" +
                "code='" + code + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
