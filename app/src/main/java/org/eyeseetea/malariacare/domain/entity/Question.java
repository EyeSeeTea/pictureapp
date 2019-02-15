package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.eyeseetea.malariacare.domain.exception.RegExpValidationException;

import java.util.List;

public class Question {
    private long id;
    private String code;
    private String name;
    private String uid;
    private PhoneFormat phoneFormat;
    private Type type;
    private boolean compulsory;
    private boolean disabled;
    private List<Option> options;
    private Header header;
    private int index;
    private Visibility visibility;
    private List<Rule> rules;
    private Value mValue;
    private String regExp;
    private String regExpError;
    private String defaultValue;

    public Question(long id, String code, String name, String uid,
            PhoneFormat phoneFormat, Type type, boolean compulsory,
            boolean disabled, List<Option> options, Header header, int index,
            Visibility visibility,
            List<Rule> rules, Value value, String regExp,
            String regExpError, String defaultValue) {

        this.id = required(id, "id is required");
        this.code = required(code, "code is required");
        this.name = required(name, "name is required");
        this.uid = required(uid, "uid is required");
        this.phoneFormat = phoneFormat;
        this.type = required(type, "type is required");
        this.compulsory = compulsory;
        this.disabled = disabled;
        this.options = options;
        this.header = header;
        this.index = index;
        this.visibility = visibility;
        this.rules = rules;
        mValue = value;
        this.regExp = regExp;
        this.regExpError = regExpError;
        this.defaultValue = defaultValue;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public List<Option> getOptions() {
        return options;
    }

    public boolean hasOptions() {
        return options != null && options.size()>0;
    }

    public Header getHeader() {
        return header;
    }

    public int getIndex() {
        return index;
    }

    public long getId() {
        return id;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getUid() {
        return uid;
    }

    public PhoneFormat getPhoneFormat() {
        return phoneFormat;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Value getValue() {
        return mValue;
    }

    public String getRegExp() {
        return regExp;
    }

    public String getRegExpError() {
        return regExpError;
    }

    public String getDefaultValue(){ return defaultValue;}

    public void match(String value) throws RegExpValidationException {
        if (!value.matches(regExp)){
            throw new RegExpValidationException(value);
        }
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
        if (regExp != null ? !regExp.equals(question.regExp) : question.regExp != null) return false;
        if (regExpError != null ? !regExpError.equals(question.regExpError) : question.regExpError != null) return false;
        if (name != null ? !name.equals(question.name) : question.name != null) return false;
        if (defaultValue != null ? !defaultValue.equals(question.defaultValue) : question.defaultValue != null) return false;
        if (uid != null ? !uid.equals(question.uid) : question.uid != null) return false;
        if (phoneFormat != null ? !phoneFormat.equals(question.phoneFormat)
                : question.phoneFormat != null) {
            return false;
        }
        if (type != question.type) return false;
        if (options != null ? !options.equals(question.options) : question.options != null) {
            return false;
        }
        if (header != null ? !header.equals(question.header) : question.header != null) {
            return false;
        }
        if (visibility != question.visibility) return false;
        return rules != null ? rules.equals(question.rules) : question.rules == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (regExp != null ? regExp.hashCode() : 0);
        result = 31 * result + (regExpError != null ? regExpError.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (defaultValue != null ? defaultValue.hashCode() : 0);
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
                ", regExp=" + regExp +
                ", regExpError=" + regExpError +
                ", defaultValue=" + defaultValue +
                '}';
    }

    public enum Type {
        SHORT_TEXT, PHONE, DROPDOWN_LIST, YEAR, DATE, INT, LONG_TEXT, POSITIVE_INT,
        PREGNANT_MONTH, RADIO_GROUP_HORIZONTAL, QUESTION_LABEL, NO_ANSWER, RADIO_GROUP_VERTICAL,
        DROPDOWN_LIST_DISABLED, IMAGES_2, IMAGES_4, IMAGES_6, IMAGES_3, IMAGES_5, COUNTER,
        WARNING, REMINDER, DROPDOWN_OU_LIST, IMAGE_3_NO_DATAELEMENT, HIDDEN,
        IMAGE_RADIO_GROUP_NO_DATAELEMENT, IMAGE_RADIO_GROUP, POSITIVE_OR_ZERO_INT,
        DYNAMIC_TREATMENT_SWITCH_NUMBER, DYNAMIC_STOCK_IMAGE_RADIO_BUTTON, PREGNANT_MONTH_INT,
        DROPDOWN_LIST_OU_TREE, SWITCH_BUTTON, AGE_MONTH_NUMBER, IMAGES_VERTICAL, AUTOCOMPLETE_TEXT
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
        private Value mValue;
        private String regExp;
        private String regExpError;
        private String defaultValue;
        private boolean disabled;

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

        public Builder disabled(boolean val) {
            disabled = val;
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

        public Builder value(Value value) {
            mValue = value;
            return this;
        }

        public Builder regExp(String val) {
            regExp = val;
            return this;
        }

        public Builder regExpError(String val) {
            regExpError = val;
            return this;
        }

        public Builder defaultValue(String val) {
            defaultValue = val;
            return this;
        }
        public Question build() {
            return new Question(
                    this.id,
                    this.code,
                    this.name,
                    this.uid,
                    this.phoneFormat,
                    this.type,
                    this.compulsory,
                    this.disabled,
                    this.options,
                    this.header,
                    this.index,
                    this.visibility,
                    this.rules,
                    mValue,
                    this.regExp,
                    this.regExpError,
                    this.defaultValue
            );
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

            this.conditions = required(conditions, "conditions is required");
            this.actions = required(actions, "actions is required");

        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public List<Condition> getConditions() {
            return conditions;
        }

        public List<Action> getActions() {
            return actions;
        }

        public static class Condition {
            private Operand left;
            private Operator operator;
            private Operand right;

            public Condition(Operand left,
                    Operator operator, Operand right) {

                this.left = required(left, "left condition is required");
                this.operator = required(operator, "operator is required");
                this.right = required(right, "right is required");
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public Operand getLeft() {
                return left;
            }

            public Operator getOperator() {
                return operator;
            }

            public Operand getRight() {
                return right;
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
                    return new Condition(
                            this.left,
                            this.operator,
                            this.right
                    );
                }
            }
        }

        public static class Operand {
            private String value;
            private OperandType type;

            public Operand(String value, OperandType type) {

                this.value = required(value, "value condition is required");
                this.type = required(type, "type condition is required");
            }

            public static Builder newBuilder() {
                return new Builder();
            }

            public String getValue() {
                return value;
            }

            public OperandType getType() {
                return type;
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
                    return new Operand(
                            this.value,
                            this.type
                    );
                }
            }
        }

        public static class Action {
            private String targetQuestion;
            private ActionToPerform actionToPerform;

            public Action(String targetQuestion,
                    ActionToPerform actionToPerform) {

                this.targetQuestion = required(targetQuestion, "value targetQuestion is required");
                this.actionToPerform =
                        required(actionToPerform, "actionToPerform condition is required");
            }


            public static Builder newBuilder() {
                return new Builder();
            }

            public String getTargetQuestion() {
                return targetQuestion;
            }

            public ActionToPerform getActionToPerform() {
                return actionToPerform;
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
                    return new Action(
                            this.targetQuestion,
                            this.actionToPerform
                    );
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
                return new Rule(
                        this.conditions,
                        this.actions
                );
            }
        }
    }

    public enum Visibility {
        VISIBLE, INVISIBLE, IMPORTANT
    }
}
