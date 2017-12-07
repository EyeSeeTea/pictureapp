package org.eyeseetea.malariacare.domain.entity;


import java.util.List;

public class Option {
    private long id;
    private String code;
    private String name;
    private Attribute attribute;
    private List<Rule> rules;


    public Option(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Option() {
    }

    public Option(long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    private Option(Builder builder) {
        setId(builder.id);
        setCode(builder.code);
        setName(builder.name);
        setAttribute(builder.attribute);
        setRules(builder.rules);
    }

    public Option(long id, String code, String name,
            Attribute attribute) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.attribute = attribute;
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
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

    public boolean hasRules(){
        return rules !=null;
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

        public Attribute() {
        }

        private Attribute(Builder builder) {
            setId(builder.id);
            setBackgroundColour(builder.backgroundColour);
            setHorizontalAlignment(builder.horizontalAlignment);
            setVerticalAlignment(builder.verticalAlignment);
            setTextSize(builder.textSize);
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

        public String getBackgroundColour() {
            return backgroundColour;
        }

        public void setBackgroundColour(String backgroundColour) {
            this.backgroundColour = backgroundColour;
        }

        public HorizontalAlignment getHorizontalAlignment() {
            return horizontalAlignment;
        }

        public void setHorizontalAlignment(
                HorizontalAlignment horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
        }

        public VerticalAlignment getVerticalAlignment() {
            return verticalAlignment;
        }

        public void setVerticalAlignment(
                VerticalAlignment verticalAlignment) {
            this.verticalAlignment = verticalAlignment;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
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
                return new Attribute(this);
            }
        }
    }

    public static class Rule{

        private Option rightOperand;
        private Operator operator;
        private Option leftOperand;

        private Action action;
        private Question actionSubject;

        public Rule(Option rightOperand,
                Operator operator, Option leftOperand,
                Action action, Question actionSubject) {
            this.rightOperand = rightOperand;
            this.operator = operator;
            this.leftOperand = leftOperand;
            this.action = action;
            this.actionSubject = actionSubject;
        }

        private Rule(Builder builder) {
            setRightOperand(builder.rightOperand);
            setOperator(builder.operator);
            setLeftOperand(builder.leftOperand);
            setAction(builder.action);
            setActionSubject(builder.actionSubject);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Option getRightOperand() {
            return rightOperand;
        }

        public void setRightOperand(Option rightOperand) {
            this.rightOperand = rightOperand;
        }

        public Operator getOperator() {
            return operator;
        }

        public void setOperator(Operator operator) {
            this.operator = operator;
        }

        public Option getLeftOperand() {
            return leftOperand;
        }

        public void setLeftOperand(Option leftOperand) {
            this.leftOperand = leftOperand;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }

        public Question getActionSubject() {
            return actionSubject;
        }

        public void setActionSubject(Question actionSubject) {
            this.actionSubject = actionSubject;
        }


        public enum Operator{
            EQUAL
        }
        public enum Action{
            SHOW
        }

        public static final class Builder {
            private Option rightOperand;
            private Operator operator;
            private Option leftOperand;
            private Action action;
            private Question actionSubject;

            private Builder() {
            }

            public Builder rightOperand(Option val) {
                rightOperand = val;
                return this;
            }

            public Builder operator(Operator val) {
                operator = val;
                return this;
            }

            public Builder leftOperand(Option val) {
                leftOperand = val;
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
                return new Rule(this);
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
            return new Option(this);
        }
    }
}
