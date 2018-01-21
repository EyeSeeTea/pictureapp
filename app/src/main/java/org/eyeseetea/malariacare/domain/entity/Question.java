package org.eyeseetea.malariacare.domain.entity;


import java.util.List;

public class Question {
    private long id;
    private String code;
    private String name;
    private String uid;
    private PhoneFormat phoneFormat;
    private Type type;
    private boolean compulsory;
    private List<Option> options;
    private Header header;
    private int index;
    private Visibility visibility;
    private List<Rule> rules;

    public Question() {
    }

    public Question(long id, String code, String name, String uid,
            PhoneFormat phoneFormat, Type type, boolean compulsory,
            List<Option> options, Header header, int index,
            Visibility visibility,
            List<Rule> rules) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.uid = uid;
        this.phoneFormat = phoneFormat;
        this.type = type;
        this.compulsory = compulsory;
        this.options = options;
        this.header = header;
        this.index = index;
        this.visibility = visibility;
        this.rules = rules;
    }

    private Question(Builder builder) {
        setId(builder.id);
        setCode(builder.code);
        setName(builder.name);
        setUid(builder.uid);
        setPhoneFormat(builder.phoneFormat);
        setType(builder.type);
        setCompulsory(builder.compulsory);
        setOptions(builder.options);
        setHeader(builder.header);
        setIndex(builder.index);
        setVisibility(builder.visibility);
        setRules(builder.rules);
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

    public boolean hasOptions() {
        return options != null;
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

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public PhoneFormat getPhoneFormat() {
        return phoneFormat;
    }

    public void setPhoneFormat(PhoneFormat phoneFormat) {
        this.phoneFormat = phoneFormat;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
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
        if (uid != null ? !uid.equals(question.uid) : question.uid != null) return false;
        if (phoneFormat != null ? !phoneFormat.equals(question.phoneFormat)
                : question.phoneFormat != null) {
            return false;
        }
        if (type != question.type) return false;
        if (options != null ? !options.equals(question.options) : question.options != null) {
            return false;
        }
        if (header != null ? !header.equals(question.header) : question.header != null)
            return false;
        if (visibility != question.visibility) return false;
        return rules != null ? rules.equals(question.rules) : question.rules == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (phoneFormat != null ? phoneFormat.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (compulsory ? 1 : 0);
        result = 31 * result + (options != null ? options.hashCode() : 0);
        result = 31 * result + (header != null ? header.hashCode() : 0);
        result = 31 * result + index;
        result = 31 * result + (visibility != null ? visibility.hashCode() : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", phoneFormat=" + phoneFormat +
                ", type=" + type +
                ", compulsory=" + compulsory +
                ", options=" + options +
                ", header=" + header +
                ", index=" + index +
                ", visibility=" + visibility +
                ", rules=" + rules +
                '}';
    }


    public enum Type {
        SHORT_TEXT, PHONE, DROPDOWN_LIST, YEAR, DATE, INT, LONG_TEXT, POSITIVE_INT,
        PREGNANT_MONTH, RADIO_GROUP_HORIZONTAL, QUESTION_LABEL, SWITCH_BUTTON
    }


    public static final class Builder {
        private String code;
        private String name;
        private String uid;
        private PhoneFormat phoneFormat;
        private Type type;
        private boolean compulsory;
        private List<Option> options;
        private Header header;
        private int index;
        private Visibility visibility;
        private List<Rule> rules;
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

        public Builder uid(String val) {
            uid = val;
            return this;
        }

        public Builder phoneFormat(PhoneFormat val) {
            phoneFormat = val;
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

        public Builder rules(List<Rule> val) {
            rules = val;
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

    public static class Rule {

        private List<Condition> conditions;
        private List<Action> actions;

        public Rule(
                List<Condition> conditions,
                List<Action> actions) {
            this.conditions = conditions;
            this.actions = actions;
        }

        private Rule(Builder builder) {
            setConditions(builder.conditions);
            setActions(builder.actions);
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public void setConditions(
                List<Condition> conditions) {
            this.conditions = conditions;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(
                List<Action> actions) {
            this.actions = actions;
        }


        public static class Condition {
            private Operand left;
            private Operator operator;
            private Operand right;

            public Condition(Operand left,
                    Operator operator, Operand right) {
                this.left = left;
                this.operator = operator;
                this.right = right;
            }

            private Condition(Builder builder) {
                setLeft(builder.left);
                setOperator(builder.operator);
                setRight(builder.right);
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public Operand getLeft() {
                return left;
            }

            public void setLeft(Operand left) {
                this.left = left;
            }

            public Operator getOperator() {
                return operator;
            }

            public void setOperator(Operator operator) {
                this.operator = operator;
            }

            public Operand getRight() {
                return right;
            }

            public void setRight(Operand right) {
                this.right = right;
            }

            public static final class Builder {
                private Operand left;
                private Operator operator;
                private Operand right;

                private Builder() {
                }

                public Builder left(Operand val) {
                    left = val;
                    return this;
                }

                public Builder operator(Operator val) {
                    operator = val;
                    return this;
                }

                public Builder right(Operand val) {
                    right = val;
                    return this;
                }

                public Condition build() {
                    return new Condition(this);
                }
            }
        }

        public static class Operand {
            private String value;
            private OperandType type;

            public Operand(String value, OperandType type) {
                this.value = value;
                this.type = type;
            }

            private Operand(Builder builder) {
                setValue(builder.value);
                setType(builder.type);
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public OperandType getType() {
                return type;
            }

            public void setType(OperandType type) {
                this.type = type;
            }

            public static final class Builder {
                private String value;
                private OperandType type;

                private Builder() {
                }

                public Builder value(String val) {
                    value = val;
                    return this;
                }

                public Builder type(OperandType val) {
                    type = val;
                    return this;
                }

                public Operand build() {
                    return new Operand(this);
                }
            }
        }

        public static class Action {
            private String targetQuestion;
            private ActionToPerform actionToPerform;

            public Action(String targetQuestion,
                    ActionToPerform actionToPerform) {
                this.targetQuestion = targetQuestion;
                this.actionToPerform = actionToPerform;
            }

            private Action(Builder builder) {
                setTargetQuestion(builder.targetQuestion);
                setActionToPerform(builder.actionToPerform);
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public String getTargetQuestion() {
                return targetQuestion;
            }

            public void setTargetQuestion(String targetQuestion) {
                this.targetQuestion = targetQuestion;
            }

            public ActionToPerform getActionToPerform() {
                return actionToPerform;
            }

            public void setActionToPerform(
                    ActionToPerform actionToPerform) {
                this.actionToPerform = actionToPerform;
            }

            public static final class Builder {
                private String targetQuestion;
                private ActionToPerform actionToPerform;

                private Builder() {
                }

                public Builder targetQuestion(String val) {
                    targetQuestion = val;
                    return this;
                }

                public Builder actionToPerform(ActionToPerform val) {
                    actionToPerform = val;
                    return this;
                }

                public Action build() {
                    return new Action(this);
                }
            }
        }

        public enum ActionToPerform {
            SHOW
        }
        public enum Operator {
            EQUAL, GREATER_THAN, GREATER_OR_EQUAL_THAN, LESS_THAN, LESS_OR_EQUAL_THAN
        }

        public enum OperandType {
            QUESTION, VALUE
        }

        public static final class Builder {
            private List<Condition> conditions;
            private List<Action> actions;

            private Builder() {
            }

            public Builder conditions(List<Condition> val) {
                conditions = val;
                return this;
            }

            public Builder actions(List<Action> val) {
                actions = val;
                return this;
            }

            public Rule build() {
                return new Rule(this);
            }
        }
    }

    public enum Visibility {
        VISIBLE, INVISIBLE, IMPORTANT
    }
}
