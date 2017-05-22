/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.database.model;

import static org.eyeseetea.malariacare.data.database.AppDatabase.answerAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.answerName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.headerAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.headerName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.optionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.optionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionRelationAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionRelationName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.tabAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.tabName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueName;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Condition;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Table(database = AppDatabase.class)
public class Question extends BaseModel {

    /**
     * Constant that reflects a visible question in information
     */
    public static final int QUESTION_VISIBLE = 1;
    /**
     * Constant that reflects a not visible question in information
     */
    public static final int QUESTION_INVISIBLE = 0;
    /**
     * Constant that reflects a visible question in information
     */
    public static final int QUESTION_COMPULSORY = 1;
    /**
     * Constant that reflects a not visible question in information
     */
    public static final int QUESTION_NOT_COMPULSORY = 0;
    private static final String TAG = "Question";
    /**
     * Required to create a null Question value to enable caching when you're the last question.
     */
    private final static Long NULL_SIBLING_ID = -0l;
    @Column
    @PrimaryKey(autoincrement = true)
    long id_question;
    @Column
    String code;
    @Column
    String de_name;
    @Column
    String help_text;
    @Column
    String form_name;
    @Column
    String uid_question;
    @Column
    Integer order_pos;
    @Column
    Float numerator_w;
    @Column
    Float denominator_w;
    @Column
    String feedback;
    @Column
    Long id_header_fk;
    /**
     * Reference to the parent header (loaded lazily)
     */
    Header header;
    @Column
    Long id_answer_fk;
    /**
     * Reference to the associated answer (loaded lazily)
     */
    Answer answer;
    @Column
    Integer output;
    //OBSOLETE
    @Column
    Long id_question_parent;
    /**
     * Reference to parent question (loaded lazily, DEPRECATED??)
     */
    Question question;
    @Column
    Long id_composite_score_fk;
    @Column
    Integer total_questions;
    @Column
    Integer visible;
    @Column
    String path;
    @Column
    Integer compulsory;
    /**
     * Reference to associated compositeScore for this question (loaded lazily)
     */
    CompositeScore compositeScore;
    /**
     * List of children questions associated to this question
     */
    List<Question> children;
    /**
     * List of questions with match "propagate_match"
     */
    List<Question> propagationQuestion;
    /**
     * List of values for this question
     */
    List<Value> values;
    /**
     * List of questionRelations of this question
     */
    List<QuestionRelation> questionRelations;
    /**
     * List of matches of this question
     */
    List<Match> matches;
    Boolean parent;
    Boolean parentHeader;
    /**
     * List of question Options of this question
     */
    private List<QuestionOption> questionOptions;
    /**
     * List of question Thresholds associated with this question
     */
    private List<QuestionThreshold> questionThresholds;
    /**
     * Cached reference to next question for this one.
     * No parent: Next question in order
     * Has parent: Next child question in order for its parent
     */
    private Question sibling;

    public Question() {
    }

    public Question(String code, String de_name, String help_text, String form_name, String uid,
            Integer order_pos, Float numerator_w, Float denominator_w, String feedback,
            Integer output, Integer compulsory, Header header, Answer answer, Question question,
            CompositeScore compositeScore) {
        this.code = code;
        this.de_name = de_name;
        this.help_text = help_text;
        this.form_name = form_name;
        this.uid_question = uid;
        this.order_pos = order_pos;
        this.numerator_w = numerator_w;
        this.denominator_w = denominator_w;
        this.feedback = feedback;
        this.output = output;
        this.compulsory = compulsory;
        this.parent = null;

        this.setHeader(header);
        this.setAnswer(answer);
        this.setCompositeScore(compositeScore);
        this.setQuestion(question);
    }

    public static List<Question> getAllQuestions() {
        return new Select().from(Question.class).queryList();
    }

    public static List<Question> getAllQuestionsWithOrgUnitDropdownList() {
        return new Select().from(Question.class)
                .where(Question_Table.output.eq(Constants.DROPDOWN_OU_LIST))
                .queryList();
    }

    public static List<Question> getAllQuestionsWithMatch() {
        return new Select(Question_Table.getAllColumnProperties()).distinct().from(
                Question.class).as(questionName)
                .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(Question_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelation_Table.id_question_fk.withTable(questionOptionAlias)))
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(Match_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelation_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .where(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelation.MATCH))
                .queryList();
    }


    private static List<Question> getAllQuestionsWithHeader(Header header) {
        return new Select()
                .from(Question.class)
                .where(Question_Table.id_header_fk
                        .eq(header.getId_header()))
                .queryList();
    }

    /**
     * Returns all the questions that belongs to a program
     */
    public static List<Question> listByProgram(Program program) {
        if (program == null || program.getId_program() == null) {
            return new ArrayList();
        }


        //return Question.findWithQuery(Question.class, LIST_ALL_BY_PROGRAM, program.getId()
        // .toString());


        return new Select().from(Question.class).as(questionName)
                .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(Question_Table.id_header_fk.withTable(questionAlias)
                        .eq(Header_Table.id_header.withTable(headerAlias)))
                .join(Tab.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(Header_Table.id_tab_fk.withTable(headerAlias)
                        .eq(Tab_Table.id_tab.withTable(tabAlias)))
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Program_Table.id_program.withTable(programAlias)
                        .eq(Tab_Table.id_program_fk.withTable(tabAlias)))
                .where(Program_Table.id_program.withTable(programAlias)
                        .eq(program.getId_program()))
                .orderBy(OrderBy.fromProperty(Tab_Table.order_pos.withTable(tabAlias)))
                .orderBy(OrderBy.fromProperty(
                        Question_Table.order_pos.withTable(questionAlias))).queryList();

    }

    public static List<Question> listAllByTabs(List<Tab> tabs) {

        if (tabs == null || tabs.size() == 0) {
            return new ArrayList();
        }

        Iterator<Tab> iterator = tabs.iterator();
        Condition.In in = Tab_Table.id_tab.withTable(tabAlias).in(iterator.next().getId_tab());
        while (iterator.hasNext()) {
            in.and(Long.toString(iterator.next().getId_tab()));
        }

        return new Select().from(Question.class).as(questionName)
                .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(Question_Table.id_header_fk.withTable(headerAlias)
                        .eq(Header_Table.id_header.withTable(headerAlias)))
                .join(Tab.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(Header_Table.id_tab_fk.withTable(headerAlias)
                        .eq(Tab_Table.id_tab.withTable(tabAlias)))
                .where(in)
                .orderBy(OrderBy.fromProperty(Tab_Table.order_pos.withTable(tabAlias)))
                .orderBy(OrderBy.fromProperty(
                        Question_Table.order_pos.withTable(questionAlias))).queryList();
    }

    /**
     * Finds a question by its UID
     */
    public static Question findByUID(String uid) {
        return new Select()
                .from(Question.class)
                .where(Question_Table.uid_question.is(uid))
                .querySingle();
    }

    /**
     * Finds a question by its ID
     */
    public static Question findByID(Long id) {
        return new Select()
                .from(Question.class)
                .where(Question_Table.id_question.is(id))
                .querySingle();
    }

    /**
     * Find the first root question in the given tab
     *
     * This cannot be done due to a dbflow join bug
     * select q.*
     * from question q
     * left join header h on q.id_header=h.id_header
     * left join questionrelation qr on q.id_question=qr.id_question
     * where h.id_tab=1 and qr.id_question is null
     * order by q.order_pos
     */
    public static Question findRootQuestion(Tab tab) {

        //Take every child question
        List<QuestionRelation> questionRelations = QuestionRelation.listAllParentChildRelations();

        if (questionRelations == null || questionRelations.size() == 0) {
            //flow without relations
            return new Select().from(Question.class).as(AppDatabase.questionName)
                    .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                    .on(Question_Table.id_header_fk.withTable(questionAlias)
                            .eq(Header_Table.id_header.withTable(headerAlias)))
                    .join(Tab.class, Join.JoinType.LEFT_OUTER).as(tabName)
                    .on(Header_Table.id_tab_fk.withTable(headerAlias)
                            .eq(Tab_Table.id_tab.withTable(tabAlias)))
                    .where(Header_Table.id_tab_fk.withTable(headerAlias)
                            .eq(tab.getId_tab()))
                    .and(Tab_Table.type.withTable(tabAlias)
                            .eq(Constants.TAB_MULTI_QUESTION))
                    .orderBy(Question_Table.order_pos.withTable(questionAlias), true)
                    .querySingle();
        }
        //Build a not in condition
        Iterator<QuestionRelation> questionRelationsIterator = questionRelations.iterator();
        Condition.In in = Question_Table.id_question.withTable(questionAlias).notIn(
                questionRelationsIterator.next().getQuestion().getId_question());
        while (questionRelationsIterator.hasNext()) {
            in.and(Long.toString(questionRelationsIterator.next().getQuestion().getId_question()));
        }

        //Look for question not in child and take first one

        return new Select().from(Question.class).as(questionName)
                .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(Question_Table.id_header_fk.withTable(questionAlias)
                        .eq(Header_Table.id_header.withTable(headerAlias)))
                .where(Header_Table.id_tab_fk.withTable(headerAlias)
                        .eq(tab.getId_tab()))
                .and(in)
                .orderBy(Question_Table.order_pos, true)
                .querySingle();
    }

    /**
     * Counts the number of required children questions by a option.
     */
    public static int countChildrenByOptionValue(long id_option) {
        return (int) SQLite.selectCountOf()
                .from(Question.class).as(questionName)

                .join(QuestionRelation.class, Join.JoinType.INNER).as(questionRelationName)
                .on(Question_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelation_Table.id_question_fk.withTable(questionRelationAlias)))

                .join(Match.class, Join.JoinType.INNER).as(matchName)
                .on(Match_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelation_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .join(QuestionOption.class, Join.JoinType.INNER).as(questionOptionName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(matchAlias)))
                .where(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.NO_ANSWER))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.COUNTER))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.QUESTION_LABEL))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.IMAGE_3_NO_DATAELEMENT))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.REMINDER))
                .and(Question_Table.output.withTable(questionAlias).isNot(
                        Constants.WARNING))
                .and(Question_Table.output.withTable(questionAlias).is(
                        QuestionRelation.PARENT_CHILD))
                .and(Question_Table.output.withTable(questionAlias).is(
                        QUESTION_COMPULSORY))
                .and(QuestionOption_Table.id_option_fk.withTable(questionOptionAlias).eq(
                        id_option))
                .count();
    }


    /**
     * Method to delete questions in cascade.
     *
     * @param questions The questions to delete.
     */
    public static void deleteQuestions(List<Question> questions) {
        for (Question question : questions) {
            QuestionOption.deleteQuestionOptions(question.getQuestionsOptions());
            QuestionThreshold.deleteQuestionThresholds(question.getQuestionsThresholds());
            QuestionRelation.deleteQuestionRelations(question.getQuestionRelations());
            question.delete();
        }
    }

    public static List<Option> getOptions(String UID) {
        List<Option> options = new Select().from(Option.class).as(optionName)
                .join(Answer.class, Join.JoinType.LEFT_OUTER).as(answerName)
                .on(Option_Table.id_answer_fk.withTable(optionAlias)
                        .eq(Answer_Table.id_answer.withTable(answerAlias)))
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Answer_Table.id_answer.withTable(answerAlias)
                        .eq(Question_Table.id_answer_fk.withTable(questionAlias)))
                .where(Question_Table.uid_question.withTable(questionAlias)
                        .eq(UID)).queryList();

        for (int i = 0; options != null && i < options.size(); i++) {
            Option currentOption = options.get(i);
            currentOption = Option.findById(currentOption.getId_option());
            options.set(i, currentOption);
        }
        return options;
    }

    public static Answer getAnswer(String questionUID) {
        return new Select().from(Answer.class).as(answerName)
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Question_Table.id_answer_fk.withTable(questionAlias)
                        .eq(Answer_Table.id_answer.withTable(answerAlias)))
                .where(Question_Table.uid_question.withTable(questionAlias)
                        .eq(questionUID)).querySingle();
    }

    /**
     * Method to get all questionOptions related by id
     */
    public List<QuestionOption> getQuestionsOptions() {
        return new Select().from(QuestionOption.class)
                .where(QuestionOption_Table.id_question_fk.eq(
                        getId_question())).queryList();
    }

    /**
     * Method to get all questionThresholds related by id
     */
    public List<QuestionThreshold> getQuestionsThresholds() {
        return new Select().from(QuestionThreshold.class)
                .where(QuestionThreshold_Table.id_question_fk.eq(
                        getId_question())).queryList();
    }

    /**
     * Creates a false question that lets cache siblings better
     */
    private Question buildNullQuestion() {
        Question noSiblingQuestion = new Question();
        noSiblingQuestion.setId_question(NULL_SIBLING_ID);
        return noSiblingQuestion;
    }

    /**
     * Tells if this is a mocked null question
     */
    private boolean isNullQuestion() {
        return NULL_SIBLING_ID.equals(this.getId_question());
    }

    public Long getId_question() {
        return id_question;
    }

    public void setId_question(Long id_question) {
        this.id_question = id_question;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDe_name() {
        return de_name;
    }

    public void setDe_name(String de_name) {
        this.de_name = de_name;
    }

    public String getInternationalizedCodeDe_Name() {
        return Utils.getInternationalizedString(de_name);
    }

    public String getHelp_text() {
        return help_text;
    }

    public void setHelp_text(String help_text) {
        this.help_text = help_text;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_name(String form_name) {
        this.form_name = form_name;
    }

    public String getInternationalizedForm_name() {
        return Utils.getInternationalizedString(form_name);
    }

    public String getUid() {
        return uid_question;
    }

    public void setUid(String uid) {
        this.uid_question = uid;
    }

    public Integer getOrder_pos() {
        return order_pos;
    }

    public void setOrder_pos(Integer order_pos) {
        this.order_pos = order_pos;
    }

    public Float getNumerator_w() {
        return numerator_w;
    }

    public void setNumerator_w(Float numerator_w) {
        this.numerator_w = numerator_w;
    }

    public Float getDenominator_w() {
        return denominator_w;
    }

    public void setDenominator_w(Float denominator_w) {
        this.denominator_w = denominator_w;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean isCompulsory() {
        return (this.compulsory == QUESTION_COMPULSORY);
    }

    public void setCompulsory(Integer compulsory) {
        this.compulsory = compulsory;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getInternationalizedPath() {
        return Utils.getInternationalizedString(path);
    }

    public Header getHeader() {
        if (header == null) {
            if (id_header_fk == null) return null;
            header = new Select()
                    .from(Header.class)
                    .where(Header_Table.id_header
                            .is(id_header_fk)).querySingle();
        }
        return header;
    }

    public void setHeader(Long id_header) {
        this.id_header_fk = id_header;
        this.header = null;
    }

    public void setHeader(Header header) {
        this.header = header;
        this.id_header_fk = (header != null) ? header.getId_header() : null;
    }

    public Long getHeaderForeingKeyId() {
        return id_header_fk;
    }
    public Integer getOutput() {
        return output;
    }

    public void setOutput(Integer output) {
        this.output = output;
    }

    public Integer getTotalQuestions() {
        return total_questions;
    }

    public void setTotalQuestions(Integer total_questions) {
        this.total_questions = total_questions;
    }

    public Boolean isVisible() {
        return (this.visible == QUESTION_VISIBLE);
    }

    public void setVisible(Integer visible) {
        this.visible = visible;
    }

    public Answer getAnswer() {
        if (answer == null) {
            if (id_answer_fk == null) return null;
            answer = new Select()
                    .from(Answer.class)
                    .where(Answer_Table.id_answer
                            .is(id_answer_fk)).querySingle();
        }
        return answer;
    }

    public void setAnswer(Long id_answer) {
        this.id_answer_fk = id_answer;
        this.answer = null;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
        this.id_answer_fk = (answer != null) ? answer.getId_answer() : null;
    }

    //Is necessary use the question relations.
    @Deprecated
    public Question getQuestion() {
        if (question == null) {
            question = new Select()
                    .from(Question.class)
                    .where(Question_Table.id_question
                            .is(id_question_parent)).querySingle();
        }
        return question;
    }

    public void setQuestion(Long id_parent) {
        this.id_question_parent = id_parent;
        this.question = null;
    }

    @Deprecated
    public void setQuestion(Question question) {
        this.question = question;
        this.id_question_parent = (question != null) ? question.getId_question() : null;
    }

    public CompositeScore getCompositeScore() {
        if (compositeScore == null) {
            if (id_composite_score_fk == null) return null;
            compositeScore = new Select()
                    .from(CompositeScore.class)
                    .where(CompositeScore_Table.id_composite_score
                            .is(id_composite_score_fk)).querySingle();
        }
        return compositeScore;
    }

    public void setCompositeScore(Long id_composite_score) {
        this.id_composite_score_fk = id_composite_score;
        this.compositeScore = null;
    }

    public void setCompositeScore(CompositeScore compositeScore) {
        this.compositeScore = compositeScore;
        this.id_composite_score_fk =
                (compositeScore != null) ? compositeScore.getId_composite_score() : null;
    }

    public List<QuestionRelation> getQuestionRelations() {
        if (questionRelations == null) {
            this.questionRelations = new Select()
                    .from(QuestionRelation.class)
                    //// FIXME: 29/12/16 https://github
                    // .com/Raizlabs/DBFlow/blob/f0d9e1710205952815db027cb560dd8868f5af0b/usage2
                    // /Indexing.md
                    //.indexedBy(Constants.QUESTION_RELATION_QUESTION_IDX)
                    .where(QuestionRelation_Table.id_question_fk
                            .eq(this.getId_question()))
                    .queryList();
        }
        return this.questionRelations;
    }

    public List<QuestionOption> getQuestionOption() {

        if (this.questionOptions == null) {
            this.questionOptions = new Select().from(QuestionOption.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_OPTION_QUESTION_IDX)
                    .where(QuestionOption_Table.id_question_fk.eq(
                            this.getId_question()))
                    .queryList();
        }
        return this.questionOptions;
    }

    public List<QuestionOption> getQuestionOptionsOfTypeMatch() {

        List<QuestionOption> matchedQuestionOption = null;

        matchedQuestionOption =
                new Select().from(QuestionOption.class).as(questionOptionName)
                        .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                        .on(QuestionOption_Table.id_match_fk.withTable(
                                questionOptionAlias)
                                .eq(Match_Table.id_match.withTable(matchAlias)))
                        .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(
                        questionRelationName)
                        .on(Match_Table.id_question_relation_fk.withTable(matchAlias)
                                .eq(QuestionRelation_Table.id_question_relation.withTable(
                                        questionRelationAlias)))
                        .where(QuestionOption_Table.id_question_fk.withTable(
                                questionOptionAlias).eq(
                                this.getId_question()))
                        .and(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(
                                QuestionRelation.MATCH))
                        .queryList();

        return matchedQuestionOption;
    }

    public List<Match> getMatches() {
        if (matches == null) {

            matches = new Select().from(Match.class).as(matchName)
                    .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                    .on(Match_Table.id_match.withTable(matchAlias)
                            .eq(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)))
                    .where(QuestionOption_Table.id_question_fk.withTable(questionOptionAlias).eq(
                            this.getId_question())).queryList();
        }
        return matches;
    }

    public List<Question> getChildren() {
        if (this.children == null) {

            //No matches no children
            List<Match> matches = getMatches();
            if (matches.size() == 0) {
                this.children = new ArrayList<>();
                return this.children;
            }

            Iterator<Match> matchesIterator = matches.iterator();
            Condition.In in = Match_Table.id_match.withTable(matchAlias)
                    .in(matchesIterator.next().getId_match());
            while (matchesIterator.hasNext()) {
                in.and(Long.toString(matchesIterator.next().getId_match()));
            }

            //Select question from questionrelation where operator=1 and id_match in (..)
            this.children = new Select().from(Question.class).as(questionName)
                    //Question + QuestioRelation
                    .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                    .on(Question_Table.id_question.withTable(questionAlias)
                            .eq(QuestionRelation_Table.id_question_fk.withTable(
                                    questionRelationAlias)))
                    //+Match
                    .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                    .on(QuestionRelation_Table.id_question_relation.withTable(questionRelationAlias)
                            .eq(Match_Table.id_question_relation_fk.withTable(matchAlias)))
                    //Parent child relationship
                    .where(in)
                    //In clause
                    .and(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(
                            QuestionRelation.PARENT_CHILD)).queryList();
        }
        return this.children;
    }

    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "values")
    public List<Value> getValues() {
        if (values == null) {
            values = new Select()
                    .from(Value.class)
                    .where(Value_Table.id_question_fk
                            .eq(this.getId_question())).queryList();
        }
        return values;
    }

    /**
     * Gets the value of this question in the current survey in session
     */
    public Value getValueBySession() {
        Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);
        return this.getValueBySurvey(survey);
    }

    public Option findOptionByValue(String value) {

        Answer answer = getAnswer();
        if (answer == null) {
            return null;
        }

        List<Option> options = answer.getOptions();

        for (Option option : options) {
            String optionCode = option.getCode();

            if (optionCode == null) {
                continue;
            }

            if (optionCode.equals(value)) {
                return option;
            }
        }

        return null;
    }

    /**
     * Gets the value of this question in the given Survey
     */
    public Value getValueBySurvey(Survey survey) {
        if (survey == null) {
            return null;
        }
        List<Value> returnValues = new Select().from(Value.class)
                //// FIXME: 29/12/16
                //.indexedBy(Constants.VALUE_IDX)
                .where(Value_Table.id_question_fk.eq(this.getId_question()))
                .and(Value_Table.id_survey_fk.eq(survey.getId_survey())).queryList();

        if (returnValues.size() == 0) {
            return null;
        } else {
            return returnValues.get(0);
        }
    }

    /**
     * Gets the option of this question in the current survey in session
     */
    public Option getOptionBySession() {
        return this.getOptionBySurvey(SurveyFragmentStrategy.getSessionSurveyByQuestion(this));
    }

    /**
     * Gets the option of this question in the given survey
     */
    public Option getOptionBySurvey(Survey survey) {
        if (survey == null) {
            return null;
        }

        Value value = this.getValueBySurvey(survey);
        if (value == null) {
            return null;
        }

        return value.getOption();
    }

    /**
     * Finds the next question to this one according to the order pos and considering children
     * questions too
     */
    public Question getSibling() {

        //Already calculated
        if (this.sibling != null) {
            Log.d(TAG, String.format("'%s'.getSibling() --cached--> '%s'", this.getCode(),
                    this.sibling.getCode()));
            return this.sibling;
        }

        //No parent -> just look for first question with upper order
        if (!this.hasParent()) {
            getSiblingNoParent();
        } else {
            getSiblingWithParent();
        }

        //Child question -> find next children question for same parent
        if (this.sibling != null && this.sibling.getCode() != null && this.getCode() != null) {
            Log.d(TAG, String.format("'%s'.getSibling() --calculated--> '%s'", this.getCode(),
                    this.sibling.getCode()));
        }
        return this.sibling.isNullQuestion() ? null : this.sibling;
    }

    private Question getSiblingWithParent() {
        //Find parent question
        QuestionOption parentQuestionOption = findParent();
        if (parentQuestionOption == null) {
            this.sibling = buildNullQuestion();
            return this.sibling;
        }
        //Find children from parent
        List<Question> siblings = parentQuestionOption.getQuestion().findChildrenByOption(
                parentQuestionOption.getOption());

        //Find current position of this
        int currentPosition = -1;
        for (int i = 0; i < siblings.size(); i++) {
            Question iQuestion = siblings.get(i);
            if (iQuestion.getId_question().equals(this.getId_question())) {
                currentPosition = i;
                break;
            }
        }

        //Last children question
        if (currentPosition == siblings.size() - 1) {
            this.sibling = buildNullQuestion();
            return this.sibling;
        }
        //Return next position
        this.sibling = siblings.get(currentPosition + 1);
        return this.sibling;
    }

    /**
     * Returns next question from same header considering the order.
     * This should not be a child question.
     */
    private Question getSiblingNoParent() {

        //Take every child question
        List<QuestionRelation> questionRelations = QuestionRelation.listAllParentChildRelations();
        //Build a not in condition
        Condition.In in;
        if (questionRelations.size() == 0) {
            //Flow without children
            in = Question_Table.id_question.withTable(questionAlias).notIn(
                    this.getId_question());
        } else {
            Iterator<QuestionRelation> questionRelationsIterator = questionRelations.iterator();
            in = Question_Table.id_question.withTable(questionAlias).notIn(
                    questionRelationsIterator.next().getQuestion().getId_question());
            while (questionRelationsIterator.hasNext()) {
                in.and(Long.toString(
                        questionRelationsIterator.next().getQuestion().getId_question()));
            }
        }

        Program questionProgram = getQuestionProgram();

        //Siblings without parents relations
        List<Question> questions = new Select().from(Question.class).as(questionName)
                .where(Question_Table.order_pos.withTable(questionAlias)
                        .greaterThan(this.getOrder_pos()))
                .and(in)
                .orderBy(OrderBy.fromProperty(Question_Table.order_pos).ascending()).queryList();

        //Doing like this because DBFLOW bug
        for (Question question : questions) {
            if (question.getQuestionProgram().equals(questionProgram)) {
                this.sibling = question;
                break;
            }
        }

        //no question behind this one -> build a null question to use cached value
        if (this.sibling == null) {
            this.sibling = buildNullQuestion();
        }
        return this.sibling;
    }

    private Program getQuestionProgram() {

        return new Select().from(Program.class).as(programName)
                .join(Tab.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(Tab_Table.id_program_fk.withTable(tabAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(Header_Table.id_tab_fk.withTable(headerAlias)
                        .eq(Tab_Table.id_tab.withTable(tabAlias)))
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Question_Table.id_header_fk.withTable(questionAlias)
                        .eq(Header_Table.id_header.withTable(headerAlias)))
                .where(Question_Table.id_question.withTable(questionAlias).eq(
                        this.getId_question())).querySingle();
    }

    public static List<Question> getQuestionsByTab(Tab tab) {
        //Select question from questionrelation where operator=1 and id_match in (..)
        return new Select().from(Question.class).as(questionName)
                //Question + QuestioRelation
                .join(Header.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(Question_Table.id_header_fk.withTable(questionAlias)
                        .eq(Header_Table.id_header.withTable(headerAlias)))
                .join(Tab.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(Header_Table.id_tab_fk.withTable(headerAlias)
                        .eq(Tab_Table.id_tab.withTable(tabAlias)))
                .where(Tab_Table.id_tab.withTable(tabAlias).eq(
                        tab.getId_tab()))
                .orderBy(OrderBy.fromProperty(Question_Table.order_pos).ascending())
                .queryList();
    }

    public List<QuestionThreshold> getQuestionThresholds() {

        if (this.questionThresholds == null) {
            this.questionThresholds = new Select().from(QuestionThreshold.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_THRESHOLDS_QUESTION_IDX)
                    .where(QuestionThreshold_Table.id_question_fk.eq(
                            this.getId_question()))
                    .queryList();
        }
        return this.questionThresholds;
    }

    public Option getAnsweredOption() {
        Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);

        Value value = Value.findValue(getId_question(), survey);
        if (value != null) {
            return Option.findById(value.getId_option());
        }
        return null;
    }

    public Option getOptionByValueInSession() {
        Option option = null;
        Value value = getValueBySession();

        if (value != null) {
            option = value.getOption();
        }
        return option;
    }

    private Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    public String getQuestionValueBySession() {
        String result = null;

        Value value = getValueBySession();

        if (value != null) {
            result = value.getValue();
        }

        return result;
    }

    /**
     * Add register to ScoreRegister if this is an scored question
     *
     * @return List</Float> {num, den}
     */
    public List<Float> initScore(Survey survey) {
        if (!this.isScored()) {
            return null;
        }

        Float num = ScoreRegister.calcNum(this, survey);
        Float denum = ScoreRegister.calcDenum(this, survey);
        ScoreRegister.addRecord(this, num, denum);
        return Arrays.asList(num, denum);
    }

    public void saveValuesDDL(Option option, Value value) {
        //No option, nothing to save
        if (option == null) {
            return;
        }
        SurveyFragmentStrategy.saveValueDDlExtraOperations(value, option, getUid());

        if (!option.getCode().equals(Constants.DEFAULT_SELECT_OPTION)) {
            Survey survey = SurveyFragmentStrategy.getSaveValuesDDLSurvey(this);

            createOrSaveDDLValue(option, value, survey);
            for (Question propagateQuestion : this.getPropagationQuestions()) {
                propagateQuestion.createOrSaveDDLValue(option,
                        Value.findValue(propagateQuestion.getId_question(),
                                Session.getMalariaSurvey()), Session.getMalariaSurvey());
            }
        } else {
            deleteValues(value);
        }
    }

    public void saveValuesText(String answer) {
        Value value = getValueBySession();
        Survey survey = (SurveyFragmentStrategy.getSessionSurveyByQuestion(this));
        SurveyFragmentStrategy.saveValuesText(value, answer, this, survey);

    }

    public void deleteValues(Value value) {
        if (value != null) {
            for (Question propagateQuestion : this.getPropagationQuestions()) {
                Value propagateValue = Value.findValueFromDatabase(
                        propagateQuestion.getId_question(),
                        Session.getMalariaSurvey());
                if (propagateValue != null) {
                    propagateValue.delete();
                }
            }
            value.delete();
        }
    }

    private void createOrSaveDDLValue(Option option, Value value,
            Survey survey) {
        if (value == null) {
            value = new Value(option, this, survey);
        } else {
            SurveyFragmentStrategy.recursiveRemover(value, option, this, survey);
            value.setOption(option);
            value.setValue(option.getCode());
        }

        value.save();
    }

    public void createOrSaveValue(String answer, Value value,
            Survey survey) {
        // If the value is not found we create one
        if (value == null) {
            value = new Value(answer, this, survey);
        } else {
            value.setOption((Long) null);
            value.setValue(answer);
        }
        value.save();
    }

    public void deleteValueBySession() {
        Value value = getValueBySession();

        if (value != null) {
            deleteValues(value);
        }
    }

    /*Returns true if the question belongs to a Custom Tab*/
    public boolean belongsToCustomTab() {

        return getHeader().getTab().isACustomTab();
    }

    /**
     * Checks if this question is shown according to the values of the given survey
     */
    public boolean isHiddenBySurvey(long idSurvey) {
        //No question relations
        if (!hasParent()) {
            return false;
        }
        long hasParentOptionActivated = SQLite.selectCountOf().from(Value.class).as(valueName)
                .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        Value_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(Match_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelation_Table.id_question_relation))
                //Parent child relationship
                .where(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(1))
                //For the given survey
                .and(Value_Table.id_survey_fk.withTable(valueAlias).eq(
                        idSurvey))
                //The child question in the relationship is 'this'
                .and(QuestionRelation_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                .count();

        //Parent with the right value -> not hidden
        return hasParentOptionActivated > 0 ? false : true;
    }

    /**
     * Checks if this question is shown according to the values of the given survey
     */
    public boolean isHiddenBySurveyAndHeader(Survey survey) {
        if (survey == null) {
            return false;
        }
        //No question relations
        if (!hasParentInSameHeader()) {
            return false;
        }
        long hasParentOptionActivated = SQLite.selectCountOf().from(Value.class).as(valueName)
                .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        Value_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(Match_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelation_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Question_Table.id_question.withTable(questionAlias)
                        .eq(QuestionOption_Table.id_question_fk.withTable(questionOptionAlias)))
                //Parent child relationship
                .where(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(1))
                //For the given survey
                .and(Value_Table.id_survey_fk.withTable(valueAlias).eq(
                        survey.getId_survey()))
                //The child question in the relationship is 'this'
                .and(QuestionRelation_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                //Group parents by header
                .and(Question_Table.id_header_fk.withTable(questionAlias).eq(
                        this.getHeader().getId_header()))
                .count();
        //Parent with the right value -> not hidden
        return hasParentOptionActivated > 0 ? false : true;
    }

    public boolean hasQuestionOption() {
        return !this.getQuestionOption().isEmpty();
    }

    public boolean hasQuestionRelations() {
        return !this.getQuestionRelations().isEmpty();
    }

    /**
     * Tells if this question has options or is an open value
     */
    public boolean hasOutputWithOptions() {
        return Constants.QUESTION_TYPES_WITH_OPTIONS.contains(this.output);

    }

    public boolean hasParent() {
        if (parent == null) {
            long countChildQuestionRelations = SQLite.selectCountOf().from(
                    QuestionRelation.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_RELATION_QUESTION_IDX)
                    .where(QuestionRelation_Table.id_question_fk.eq(
                            this.getId_question()))
                    .and(QuestionRelation_Table.operation.eq(
                            QuestionRelation.PARENT_CHILD))
                    .count();
            parent = Boolean.valueOf(countChildQuestionRelations > 0);
        }
        return parent;
    }

    public boolean hasParentInSameHeader() {
        //FIXME: this method is by hand doing something that might be done with a DB query
        if (parentHeader == null) {
            parentHeader = false;

            List<Question> questions = Question.getAllQuestionsWithHeader(getHeader());
            //Only one its itself.
            if (questions == null || questions.size() <= 1) {
                return false;
            }

            //Removes itself
            questions.remove(this);

            for (Question question : questions) {
                for (QuestionOption questionOption : question.getQuestionOption()) {
                    Match match = questionOption.getMatch();
                    QuestionRelation questionRelation = match.getQuestionRelation();
                    if (questionRelation.getOperation() == QuestionRelation.PARENT_CHILD
                            && questionRelation.getQuestion().getId_question().equals(
                            this.getId_question())) {
                        parentHeader = true;
                        return parentHeader;
                    }
                }
            }
        }
        return parentHeader;
    }

    public boolean hasQuestionThresholds() {
        return !this.getQuestionThresholds().isEmpty();
    }

    /**
     * Checks if this question is triggered according to the current values of the given survey.
     * Only applies to question with answers DROPDOWN_DISABLED
     */
    public boolean isTriggered(float idSurvey) {

        //Only disabled dropdowns
        if (this.getOutput() != Constants.DROPDOWN_LIST_DISABLED) {
            return false;
        }

        //Find questionoptions for q1 and q2 and check same match
        List<QuestionOption> questionOptions = new Select().from(QuestionOption.class).as(
                questionOptionName)
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias).eq(
                        Match_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(Match_Table.id_question_relation_fk.withTable(matchAlias).eq(
                        QuestionRelation_Table.id_question_relation))

                .join(Value.class, Join.JoinType.LEFT_OUTER).as(valueName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        Value_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOption_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .where(Value_Table.id_survey_fk.withTable(valueAlias).eq((long) idSurvey))
                .and(QuestionRelation_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                .and(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelation.MATCH))
                .queryList();

        //No values no match
        if (questionOptions.size() != 2) {
            return false;
        }

        //Match is triggered if questionoptions have same matchid
        long idmatchQ1 = questionOptions.get(0).getMatch().getId_match();
        long idmatchQ2 = questionOptions.get(1).getMatch().getId_match();
        return idmatchQ1 == idmatchQ2;

    }

    /**
     * Checks if this question is scored or not.
     *
     * @return true|false
     */
    public boolean isScored() {
        Integer output = getOutput();
        return output == Constants.DROPDOWN_LIST ||
                output == Constants.DROPDOWN_LIST_DISABLED ||
                output == Constants.RADIO_GROUP_HORIZONTAL ||
                output == Constants.RADIO_GROUP_VERTICAL;
    }

    public boolean hasAssociatedImage() {
        return (getPath() != null && !getPath().equals(""));
    }

    /**
     * Find the first children question for this question taking into the account the given
     * option
     */
    public Question findFirstChildrenByOption(Option option) {
        List<Question> childrenQuestions = findChildrenByOption(option);
        if (childrenQuestions == null || childrenQuestions.size() == 0) {
            return null;
        }

        return childrenQuestions.get(0);
    }

    /**
     * Find the children questions for this question taking into the account the given option
     */
    public List<Question> findChildrenByOption(Option option) {
        List<Question> childrenQuestions = new ArrayList<>();
        //No option -> no children
        if (option == null) {
            return childrenQuestions;
        }

        List<QuestionOption> questionOptions = this.getQuestionOption();
        //No trigger (questionOption) -> no children
        if (questionOptions == null || questionOptions.size() == 0) {
            return childrenQuestions;
        }

        //Navigate to questionRelation to get child questions
        long optionId = option.getId_option().longValue();
        for (QuestionOption questionOption : questionOptions) {
            //Other options must be discarded
            if (discardOptions(optionId, questionOption)) continue;
            Match match = questionOption.getMatch();
            if (match == null) {
                continue;
            }

            QuestionRelation questionRelation = match.getQuestionRelation();
            //only parent child are interesting for this
            if (questionRelation == null
                    || questionRelation.getOperation() != QuestionRelation.PARENT_CHILD) {
                continue;
            }

            Question childQuestion = questionRelation.getQuestion();
            if (childQuestion == null) {
                continue;
            }
            childrenQuestions.add(childQuestion);
        }

        //Sort asc by order pos
        Collections.sort(childrenQuestions, new QuestionOrderComparator());

        return childrenQuestions;
    }

    private boolean discardOptions(long optionId, QuestionOption questionOption) {
        if (questionOption.getOption() == null) {
            return true;
        }
        long currentOptionId = questionOption.getOption().getId_option().longValue();
        if (optionId != currentOptionId) {
            return true;
        }
        return false;
    }

    public Value insertValue(String value, Survey survey) {
        return new Value(value, this, survey);
    }

    /**
     * Find the counter question for this question taking into the account the given option.
     * Only 1 counter question will be activated by option
     */
    public Question findCounterByOption(Option option) {

        //No option -> no children
        if (option == null) {
            return null;
        }

        List<QuestionOption> questionOptions = this.getQuestionOption();
        //No trigger (questionOption) -> no counters
        if (questionOptions == null || questionOptions.size() == 0) {
            return null;
        }

        //Navigate to questionRelation to get child questions
        long optionId = option.getId_option().longValue();
        for (QuestionOption questionOption : questionOptions) {
            //Other options must be discarded
            if (discardOptions(optionId, questionOption)) continue;
            Match match = questionOption.getMatch();
            if (match == null) {
                continue;
            }

            QuestionRelation questionRelation = match.getQuestionRelation();
            //only COUNTER RELATIONSHIPs are interesting for this
            if (questionRelation == null
                    || questionRelation.getOperation() != QuestionRelation.COUNTER) {
                continue;
            }

            Question childQuestion = questionRelation.getQuestion();
            if (childQuestion == null) {
                continue;
            }

            //Found
            return childQuestion;
        }

        return null;
    }

    /**
     * Returns a list of questionOptions that activates this question (might be several
     * though it
     * tends to be just one)
     */
    public List<QuestionOption> findParents() {
        List<QuestionOption> parents = new ArrayList<>();
        //No potential parents
        if (!this.hasParent()) {
            return parents;
        }

        //Add parents via (questionrelation->match->questionoption->question
        List<QuestionRelation> questionRelations = this.getQuestionRelations();
        for (QuestionRelation questionRelation : questionRelations) {
            //Only parentchild relationships
            if (questionRelation.getOperation() != QuestionRelation.PARENT_CHILD) {
                continue;
            }
            for (Match match : questionRelation.getMatches()) {
                parents.addAll(match.getQuestionOptions());
            }
        }

        return parents;
    }

    /**
     * Returns the first parent candidate.
     * XXX: This is a shortcut considering that there wont be more than parent but this is not
     * guaranteed
     */
    public QuestionOption findParent() {

        List<QuestionOption> parents = findParents();
        if (parents == null || parents.size() == 0) {
            return null;
        }
        return parents.get(0);
    }

    public boolean isAnswered() {
        return (this.getValueBySession() != null);
    }

    public boolean isNotAnswered(Question question) {
        if (question.getValueBySession() == null
                || question.getValueBySession().getValue() == null
                || question.getValueBySession().getValue().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the question is a counter or not
     */
    public boolean isACounter() {
        QuestionRelation questionRelation = new Select().from(QuestionRelation.class).where(
                QuestionRelation_Table.operation.eq(
                        QuestionRelation.COUNTER)).and(
                QuestionRelation_Table.id_question_fk.eq(
                        this.getId_question())).querySingle();
        return questionRelation != null;
    }


    public boolean hasCompulsoryNotAnswered() {
        //get all the questions in the same screen page

        List<Question> questions = SurveyFragmentStrategy.getCompulsoryNotAnsweredQuestions(this);
        if (questions.size() == 0) {
            return true;
        }
        for (Question question : questions) {
            Survey survey = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);
            if (question.isCompulsory() && !question.isHiddenBySurveyAndHeader(
                    survey) && isNotAnswered(question)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDataElement() {
        return !Constants.QUESTION_TYPES_NO_DATA_ELEMENT.contains(output);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question = (Question) o;

        if (id_question != question.id_question) return false;
        if (code != null ? !code.equals(question.code) : question.code != null) return false;
        if (de_name != null ? !de_name.equals(question.de_name) : question.de_name != null) {
            return false;
        }
        if (help_text != null ? !help_text.equals(question.help_text)
                : question.help_text != null) {
            return false;
        }
        if (form_name != null ? !form_name.equals(question.form_name)
                : question.form_name != null) {
            return false;
        }
        if (uid_question != null ? !uid_question.equals(question.uid_question)
                : question.uid_question != null) {
            return false;
        }
        if (order_pos != null ? !order_pos.equals(question.order_pos)
                : question.order_pos != null) {
            return false;
        }
        if (numerator_w != null ? !numerator_w.equals(question.numerator_w)
                : question.numerator_w != null) {
            return false;
        }
        if (denominator_w != null ? !denominator_w.equals(question.denominator_w)
                : question.denominator_w != null) {
            return false;
        }
        if (feedback != null ? !feedback.equals(question.feedback)
                : question.feedback != null) {
            return false;
        }
        if (id_header_fk != null ? !id_header_fk.equals(question.id_header_fk)
                : question.id_header_fk != null) {
            return false;
        }
        if (id_answer_fk != null ? !id_answer_fk.equals(question.id_answer_fk)
                : question.id_answer_fk != null) {
            return false;
        }
        if (output != null ? !output.equals(question.output) : question.output != null) {
            return false;
        }
        if (id_question_parent != null ? !id_question_parent.equals(question.id_question_parent)
                : question.id_question_parent != null) {
            return false;
        }
        if (path != null ? !path.equals(question.path) : question.path != null) {
            return false;
        }
        if (total_questions != null ? !total_questions.equals(question.total_questions)
                : question.total_questions != null) {
            return false;
        }
        if (visible != null ? !visible.equals(question.visible) : question.visible != null) {
            return false;
        }
        if (compulsory != null ? !compulsory.equals(question.compulsory)
                : question.compulsory != null) {
            return false;
        }
        return !(id_composite_score_fk != null ? !id_composite_score_fk.equals(
                question.id_composite_score_fk) : question.id_composite_score_fk != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_question ^ (id_question >>> 32));
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (de_name != null ? de_name.hashCode() : 0);
        result = 31 * result + (help_text != null ? help_text.hashCode() : 0);
        result = 31 * result + (form_name != null ? form_name.hashCode() : 0);
        result = 31 * result + (uid_question != null ? uid_question.hashCode() : 0);
        result = 31 * result + (order_pos != null ? order_pos.hashCode() : 0);
        result = 31 * result + (numerator_w != null ? numerator_w.hashCode() : 0);
        result = 31 * result + (denominator_w != null ? denominator_w.hashCode() : 0);
        result = 31 * result + (feedback != null ? feedback.hashCode() : 0);
        result = 31 * result + (id_header_fk != null ? id_header_fk.hashCode() : 0);
        result = 31 * result + (id_answer_fk != null ? id_answer_fk.hashCode() : 0);
        result = 31 * result + (output != null ? output.hashCode() : 0);
        result = 31 * result + (id_question_parent != null ? id_question_parent.hashCode() : 0);
        result = 31 * result + (id_composite_score_fk != null ? id_composite_score_fk.hashCode()
                : 0);
        result = 31 * result + (visible != null ? visible.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (total_questions != null ? total_questions.hashCode() : 0);
        result = 31 * result + (compulsory != null ? compulsory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id_question=" + id_question +
                ", code='" + code + " " + '\'' +
                ", de_name='" + de_name + " " + '\'' +
                ", help_text='" + help_text + " " + '\'' +
                ", form_name='" + form_name + " " + '\'' +
                ", uid_question='" + uid_question + " " + '\'' +
                ", order_pos=" + order_pos +
                ", numerator_w=" + numerator_w +
                ", feedback='" + feedback + " " + '\'' +
                ", denominator_w=" + denominator_w +
                ", id_header=" + id_header_fk +
                ", id_answer=" + id_answer_fk +
                ", compulsory=" + compulsory +
                ", output=" + output +
                ", id_question_parent=" + id_question_parent +
                ", id_composite_score=" + id_composite_score_fk +
                ", total_questions=" + total_questions +
                ", visible=" + visible +
                ", path=" + path +
                '}';
    }

    public List<Question> getPropagationQuestions() {
        if (propagationQuestion != null) {
            return propagationQuestion;
        }
        //No matches no children
        List<Match> matches = getMatches();
        if (matches.size() == 0) {
            this.propagationQuestion = new ArrayList<>();
            return this.propagationQuestion;
        }
        //Select question from questionrelation where operator=1 and id_match in (..)
        propagationQuestion = new Select().from(Question.class).as(questionName)
                //Question + QuestioRelation
                .join(QuestionRelation.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(Question_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelation_Table.id_question_fk.withTable(
                                questionRelationAlias)))
                //+Match
                .join(Match.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionRelation_Table.id_question_relation.withTable(questionRelationAlias)
                        .eq(Match_Table.id_question_relation_fk.withTable(matchAlias)))
                //+Questionoption
                .join(QuestionOption.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(Match_Table.id_match.withTable(matchAlias)))
                //Parent child relationship
                .where(QuestionRelation_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelation.MATCH_PROPAGATE))
                .and(QuestionOption_Table.id_question_fk.withTable(questionOptionAlias).is(
                        id_question)).queryList();
        return propagationQuestion;
    }

    private static class QuestionOrderComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            Question question1 = (Question) o1;
            Question question2 = (Question) o2;

            return new Integer(
                    question1.getOrder_pos().compareTo(new Integer(question2.getOrder_pos())));
        }
    }
}