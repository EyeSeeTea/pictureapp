package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.List;

public class Option {
    private long id;
    private String code;
    private String name;
    private Attribute attribute;
    private List<Rule> rules;

    public Option(long id, String code, String name,
            Attribute attribute,
            List<Rule> rules) {
        this.id = required(id, "id is required");
        this.code = required(code, "code is required");
        this.name = required(name, "name is required");
        this.attribute = attribute;
        this.rules = rules;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public long getId() {
        return id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Option option = (Option) o;

        if (id != option.id) return false;
        if (code != null ? !code.equals(option.code) : option.code != null) return false;
        if (name != null ? !name.equals(option.name) : option.name != null) return false;
        return attribute != null ? attribute.equals(option.attribute) : option.attribute == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (attribute != null ? attribute.hashCode() : 0);
        return result;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean hasRules() {
        return rules != null;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "Option{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", attribute=" + attribute +
                ", rules=" + rules +
                '}';
    }

    public static class Attribute {
        private long id;
        private String backgroundColour;
        private HorizontalAlignment horizontalAlignment;
        private VerticalAlignment verticalAlignment;
        private int textSize;

        public Attribute(long id, String backgroundColour,
                HorizontalAlignment horizontalAlignment,
                VerticalAlignment verticalAlignment, int textSize) {

            this.id = id;
            this.backgroundColour = backgroundColour;
            this.horizontalAlignment = horizontalAlignment;
            this.verticalAlignment = verticalAlignment;
            this.textSize = textSize;
        }


        public static Builder newBuilder() {
            return new Builder();
        }

        public long getId() {
            return id;
        }

        public String getBackgroundColour() {
            return backgroundColour;
        }

        public HorizontalAlignment getHorizontalAlignment() {
            return horizontalAlignment;
        }

        public VerticalAlignment getVerticalAlignment() {
            return verticalAlignment;
        }

        public int getTextSize() {
            return textSize;
        }

        public enum HorizontalAlignment {
            LEFT, CENTER, RIGHT, NONE;
        }

        public enum VerticalAlignment {
            TOP, MIDDLE, BOTTOM, NONE;
        }

        public static final class Builder {
            private long id;
            private String backgroundColour;
            private HorizontalAlignment horizontalAlignment;
            private VerticalAlignment verticalAlignment;
            private int textSize;

            private Builder() {
            }

            public Builder id(long val) {
                id = val;
                return this;
            }

            public Builder backgroundColour(String val) {
                backgroundColour = val;
                return this;
            }

            public Builder horizontalAlignment(HorizontalAlignment val) {
                horizontalAlignment = val;
                return this;
            }

            public Builder verticalAlignment(VerticalAlignment val) {
                verticalAlignment = val;
                return this;
            }

            public Builder textSize(int val) {
                textSize = val;
                return this;
            }

            public Attribute build() {
                return new Attribute(
                        this.id,
                        this.backgroundColour,
                        this.horizontalAlignment,
                        this.verticalAlignment,
                        this.textSize
                );
            }
        }
    }

    public static class Rule {

        private Operator operator;
        private Action action;
        private Question actionSubject;

        public Rule(Action action, Operator operator, Question actionSubject) {
            this.action = required(action, "action is required");
            this.operator = required(operator, "operator is required");
            this.actionSubject = required(actionSubject, "actionSubject is required");
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Operator getOperator() {
            return operator;
        }

        public Action getAction() {
            return action;
        }

        public Question getActionSubject() {
            return actionSubject;
        }

        public enum Operator {
            EQUAL
        }

        public enum Action {
            SHOW
        }

        public static final class Builder {
            private Operator operator;
            private Action action;
            private Question actionSubject;

            private Builder() {
            }


            public Builder operator(Operator val) {
                operator = val;
                return this;
            }


            public Builder action(Action val) {
                action = val;
                return this;
            }

            public Builder actionSubject(Question val) {
                actionSubject = val;
                return this;
            }

            public Rule build() {
                return new Rule(
                        this.action,
                        this.operator,
                        this.actionSubject
                );
            }
        }
    }

    public static final class Builder {
        private long id;
        private String code;
        private String name;
        private Attribute attribute;
        private List<Rule> rules;

        private Builder() {
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder attribute(Attribute val) {
            attribute = val;
            return this;
        }

        public Builder rules(List<Rule> val) {
            rules = val;
            return this;
        }

        public Option build() {
            return new Option(
                    this.id,
                    this.code,
                    this.name,
                    this.attribute,
                    this.rules
            );
        }
    }
}
