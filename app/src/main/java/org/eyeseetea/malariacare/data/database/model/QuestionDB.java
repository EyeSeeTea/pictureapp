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

@Table(database = AppDatabase.class, name = "Question")
public class QuestionDB extends BaseModel {

    /**
     * Constant that reflects a visible mQuestionDB in information
     */
    public static final int QUESTION_VISIBLE = 1;
    /**
     * Constant that reflects a not visible mQuestionDB in information
     */
    public static final int QUESTION_INVISIBLE = 0;
    /**
     * Constant that reflects a visible mQuestionDB in information
     */
    public static final int QUESTION_COMPULSORY = 1;
    /**
     * Constant that reflects a not visible mQuestionDB in information
     */
    public static final int QUESTION_NOT_COMPULSORY = 0;
    private static final String TAG = "Question";
    /**
     * Required to create a null QuestionDB value to enable caching when you're the last mQuestionDB.
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
     * Reference to the parent mHeaderDB (loaded lazily)
     */
    HeaderDB mHeaderDB;
    @Column
    Long id_answer_fk;
    /**
     * Reference to the associated mAnswerDB (loaded lazily)
     */
    AnswerDB mAnswerDB;
    @Column
    Integer output;
    //OBSOLETE
    @Column
    Long id_question_parent;
    /**
     * Reference to parent mQuestionDB (loaded lazily, DEPRECATED??)
     */
    QuestionDB mQuestionDB;
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
     * Reference to associated mCompositeScoreDB for this mQuestionDB (loaded lazily)
     */
    CompositeScoreDB mCompositeScoreDB;
    /**
     * List of children mQuestionDBs associated to this mQuestionDB
     */
    List<QuestionDB> children;
    /**
     * List of mQuestionDBs with mMatchDB "propagate_match"
     */
    List<QuestionDB> mPropagationQuestionDB;
    /**
     * List of values for this mQuestionDB
     */
    List<ValueDB> values;
    /**
     * List of mQuestionRelationDBs of this mQuestionDB
     */
    List<QuestionRelationDB> mQuestionRelationDBs;
    /**
     * List of mMatchDBs of this mQuestionDB
     */
    List<MatchDB> mMatchDBs;
    Boolean parent;
    Boolean parentHeader;
    /**
     * List of mQuestionDB Options of this mQuestionDB
     */
    private List<QuestionOptionDB> mQuestionOptionDBs;
    /**
     * List of mQuestionDB Thresholds associated with this mQuestionDB
     */
    private List<QuestionThresholdDB> mQuestionThresholdDBs;
    /**
     * Cached reference to next mQuestionDB for this one.
     * No parent: Next mQuestionDB in order
     * Has parent: Next child mQuestionDB in order for its parent
     */
    private QuestionDB sibling;

    public QuestionDB() {
    }

    public QuestionDB(String code, String de_name, String help_text, String form_name, String uid,
            Integer order_pos, Float numerator_w, Float denominator_w, String feedback,
            Integer output, Integer compulsory, HeaderDB headerDB, AnswerDB answerDB,
            QuestionDB questionDB,
            CompositeScoreDB compositeScoreDB) {
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

        this.setHeader(headerDB);
        this.setAnswer(answerDB);
        this.setCompositeScore(compositeScoreDB);
        this.setQuestion(questionDB);
    }

    public static List<QuestionDB> getAllQuestions() {
        return new Select().from(QuestionDB.class).queryList();
    }

    public static List<QuestionDB> getAllQuestionsWithOrgUnitDropdownList() {
        return new Select().from(QuestionDB.class)
                .where(QuestionDB_Table.output.eq(Constants.DROPDOWN_OU_LIST))
                .queryList();
    }

    public static List<QuestionDB> getAllQuestionsWithMatch() {
        return new Select(QuestionDB_Table.getAllColumnProperties()).distinct().from(
                QuestionDB.class).as(questionName)
                .join(QuestionOptionDB.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(QuestionDB_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelationDB_Table.id_question_fk.withTable(questionOptionAlias)))
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelationDB_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .where(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelationDB.MATCH))
                .queryList();
    }


    private static List<QuestionDB> getAllQuestionsWithHeader(HeaderDB headerDB) {
        return new Select()
                .from(QuestionDB.class)
                .where(QuestionDB_Table.id_header_fk
                        .eq(headerDB.getId_header()))
                .queryList();
    }

    /**
     * Returns all the mQuestionDBs that belongs to a programDB
     */
    public static List<QuestionDB> listByProgram(ProgramDB programDB) {
        if (programDB == null || programDB.getId_program() == null) {
            return new ArrayList();
        }


        //return QuestionDB.findWithQuery(QuestionDB.class, LIST_ALL_BY_PROGRAM, programDB.getId()
        // .toString());


        return new Select().from(QuestionDB.class).as(questionName)
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(ProgramDB_Table.id_program.withTable(programAlias)
                        .eq(TabDB_Table.id_program_fk.withTable(tabAlias)))
                .where(ProgramDB_Table.id_program.withTable(programAlias)
                        .eq(programDB.getId_program()))
                .orderBy(OrderBy.fromProperty(TabDB_Table.order_pos.withTable(tabAlias)))
                .orderBy(OrderBy.fromProperty(
                        QuestionDB_Table.order_pos.withTable(questionAlias))).queryList();

    }

    public static List<QuestionDB> listAllByTabs(List<TabDB> tabDBs) {

        if (tabDBs == null || tabDBs.size() == 0) {
            return new ArrayList();
        }

        Iterator<TabDB> iterator = tabDBs.iterator();
        Condition.In in = TabDB_Table.id_tab.withTable(tabAlias).in(iterator.next().getId_tab());
        while (iterator.hasNext()) {
            in.and(Long.toString(iterator.next().getId_tab()));
        }

        return new Select().from(QuestionDB.class).as(questionName)
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(QuestionDB_Table.id_header_fk.withTable(headerAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                .where(in)
                .orderBy(OrderBy.fromProperty(TabDB_Table.order_pos.withTable(tabAlias)))
                .orderBy(OrderBy.fromProperty(
                        QuestionDB_Table.order_pos.withTable(questionAlias))).queryList();
    }

    /**
     * Finds a mQuestionDB by its UID
     */
    public static QuestionDB findByUID(String uid) {
        return new Select()
                .from(QuestionDB.class)
                .where(QuestionDB_Table.uid_question.is(uid))
                .querySingle();
    }

    /**
     * Finds a mQuestionDB by its ID
     */
    public static QuestionDB findByID(Long id) {
        return new Select()
                .from(QuestionDB.class)
                .where(QuestionDB_Table.id_question.is(id))
                .querySingle();
    }

    /**
     * Find the first root mQuestionDB in the given tabDB
     *
     * This cannot be done due to a dbflow join bug
     * select q.*
     * from mQuestionDB q
     * left join mHeaderDB h on q.id_header=h.id_header
     * left join questionrelation qr on q.id_question=qr.id_question
     * where h.id_tab=1 and qr.id_question is null
     * order by q.order_pos
     */
    public static QuestionDB findRootQuestion(TabDB tabDB) {

        //Take every child mQuestionDB
        List<QuestionRelationDB> questionRelationDBs = QuestionRelationDB.listAllParentChildRelations();

        if (questionRelationDBs == null || questionRelationDBs.size() == 0) {
            //flow without relations
            return new Select().from(QuestionDB.class).as(AppDatabase.questionName)
                    .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                    .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                            .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                    .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                    .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                            .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                    .where(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                            .eq(tabDB.getId_tab()))
                    .and(TabDB_Table.type.withTable(tabAlias)
                            .eq(Constants.TAB_MULTI_QUESTION))
                    .orderBy(QuestionDB_Table.order_pos.withTable(questionAlias), true)
                    .querySingle();
        }
        //Build a not in condition
        Iterator<QuestionRelationDB> questionRelationsIterator = questionRelationDBs.iterator();
        Condition.In in = QuestionDB_Table.id_question.withTable(questionAlias).notIn(
                questionRelationsIterator.next().getQuestionDB().getId_question());
        while (questionRelationsIterator.hasNext()) {
            in.and(Long.toString(
                    questionRelationsIterator.next().getQuestionDB().getId_question()));
        }

        //Look for mQuestionDB not in child and take first one

        return new Select().from(QuestionDB.class).as(questionName)
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .where(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(tabDB.getId_tab()))
                .and(in)
                .orderBy(QuestionDB_Table.order_pos, true)
                .querySingle();
    }

    /**
     * Counts the number of required children mQuestionDBs by a mOptionDB.
     */
    public static int countChildrenByOptionValue(long id_option) {
        return (int) SQLite.selectCountOf()
                .from(QuestionDB.class).as(questionName)

                .join(QuestionRelationDB.class, Join.JoinType.INNER).as(questionRelationName)
                .on(QuestionDB_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelationDB_Table.id_question_fk.withTable(questionRelationAlias)))

                .join(MatchDB.class, Join.JoinType.INNER).as(matchName)
                .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelationDB_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .join(QuestionOptionDB.class, Join.JoinType.INNER).as(questionOptionName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                .where(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.NO_ANSWER))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.COUNTER))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.QUESTION_LABEL))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.IMAGE_3_NO_DATAELEMENT))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.REMINDER))
                .and(QuestionDB_Table.output.withTable(questionAlias).isNot(
                        Constants.WARNING))
                .and(QuestionDB_Table.output.withTable(questionAlias).is(
                        QuestionRelationDB.PARENT_CHILD))
                .and(QuestionDB_Table.output.withTable(questionAlias).is(
                        QUESTION_COMPULSORY))
                .and(QuestionOptionDB_Table.id_option_fk.withTable(questionOptionAlias).eq(
                        id_option))
                .count();
    }


    /**
     * Method to delete mQuestionDBs in cascade.
     *
     * @param questionDBs The mQuestionDBs to delete.
     */
    public static void deleteQuestions(List<QuestionDB> questionDBs) {
        for (QuestionDB questionDB : questionDBs) {
            QuestionOptionDB.deleteQuestionOptions(questionDB.getQuestionsOptions());
            QuestionThresholdDB.deleteQuestionThresholds(questionDB.getQuestionsThresholds());
            QuestionRelationDB.deleteQuestionRelations(questionDB.getQuestionRelationDBs());
            questionDB.delete();
        }
    }

    public static List<OptionDB> getOptions(String UID) {
        List<OptionDB> optionDBs = new Select().from(OptionDB.class).as(optionName)
                .join(AnswerDB.class, Join.JoinType.LEFT_OUTER).as(answerName)
                .on(OptionDB_Table.id_answer_fk.withTable(optionAlias)
                        .eq(AnswerDB_Table.id_answer.withTable(answerAlias)))
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(AnswerDB_Table.id_answer.withTable(answerAlias)
                        .eq(QuestionDB_Table.id_answer_fk.withTable(questionAlias)))
                .where(QuestionDB_Table.uid_question.withTable(questionAlias)
                        .eq(UID)).queryList();

        for (int i = 0; optionDBs != null && i < optionDBs.size(); i++) {
            OptionDB currentOptionDB = optionDBs.get(i);
            currentOptionDB = OptionDB.findById(currentOptionDB.getId_option());
            optionDBs.set(i, currentOptionDB);
        }
        return optionDBs;
    }

    public static AnswerDB getAnswer(String questionUID) {
        return new Select().from(AnswerDB.class).as(answerName)
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(QuestionDB_Table.id_answer_fk.withTable(questionAlias)
                        .eq(AnswerDB_Table.id_answer.withTable(answerAlias)))
                .where(QuestionDB_Table.uid_question.withTable(questionAlias)
                        .eq(questionUID)).querySingle();
    }

    /**
     * Method to get all mQuestionOptionDBs related by id
     */
    public List<QuestionOptionDB> getQuestionsOptions() {
        return new Select().from(QuestionOptionDB.class)
                .where(QuestionOptionDB_Table.id_question_fk.eq(
                        getId_question())).queryList();
    }

    /**
     * Method to get all mQuestionThresholdDBs related by id
     */
    public List<QuestionThresholdDB> getQuestionsThresholds() {
        return new Select().from(QuestionThresholdDB.class)
                .where(QuestionThresholdDB_Table.id_question_fk.eq(
                        getId_question())).queryList();
    }

    /**
     * Creates a false mQuestionDB that lets cache siblings better
     */
    private QuestionDB buildNullQuestion() {
        QuestionDB noSiblingQuestionDB = new QuestionDB();
        noSiblingQuestionDB.setId_question(NULL_SIBLING_ID);
        return noSiblingQuestionDB;
    }

    /**
     * Tells if this is a mocked null mQuestionDB
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

    public HeaderDB getHeaderDB() {
        if (mHeaderDB == null) {
            if (id_header_fk == null) return null;
            mHeaderDB = new Select()
                    .from(HeaderDB.class)
                    .where(HeaderDB_Table.id_header
                            .is(id_header_fk)).querySingle();
        }
        return mHeaderDB;
    }

    public void setHeaderDB(Long id_header) {
        this.id_header_fk = id_header;
        this.mHeaderDB = null;
    }

    public void setHeader(HeaderDB headerDB) {
        this.mHeaderDB = headerDB;
        this.id_header_fk = (headerDB != null) ? headerDB.getId_header() : null;
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

    public AnswerDB getAnswerDB() {
        if (mAnswerDB == null) {
            if (id_answer_fk == null) return null;
            mAnswerDB = new Select()
                    .from(AnswerDB.class)
                    .where(AnswerDB_Table.id_answer
                            .is(id_answer_fk)).querySingle();
        }
        return mAnswerDB;
    }

    public void setAnswerDB(Long id_answer) {
        this.id_answer_fk = id_answer;
        this.mAnswerDB = null;
    }

    public void setAnswer(AnswerDB answerDB) {
        this.mAnswerDB = answerDB;
        this.id_answer_fk = (answerDB != null) ? answerDB.getId_answer() : null;
    }

    //Is necessary use the mQuestionDB relations.
    @Deprecated
    public QuestionDB getQuestionDB() {
        if (mQuestionDB == null) {
            mQuestionDB = new Select()
                    .from(QuestionDB.class)
                    .where(QuestionDB_Table.id_question
                            .is(id_question_parent)).querySingle();
        }
        return mQuestionDB;
    }

    public void setQuestionDB(Long id_parent) {
        this.id_question_parent = id_parent;
        this.mQuestionDB = null;
    }

    @Deprecated
    public void setQuestion(QuestionDB questionDB) {
        this.mQuestionDB = questionDB;
        this.id_question_parent = (questionDB != null) ? questionDB.getId_question() : null;
    }

    public CompositeScoreDB getCompositeScoreDB() {
        if (mCompositeScoreDB == null) {
            if (id_composite_score_fk == null) return null;
            mCompositeScoreDB = new Select()
                    .from(CompositeScoreDB.class)
                    .where(CompositeScoreDB_Table.id_composite_score
                            .is(id_composite_score_fk)).querySingle();
        }
        return mCompositeScoreDB;
    }

    public void setCompositeScoreDB(Long id_composite_score) {
        this.id_composite_score_fk = id_composite_score;
        this.mCompositeScoreDB = null;
    }

    public void setCompositeScore(CompositeScoreDB compositeScoreDB) {
        this.mCompositeScoreDB = compositeScoreDB;
        this.id_composite_score_fk =
                (compositeScoreDB != null) ? compositeScoreDB.getId_composite_score() : null;
    }

    public List<QuestionRelationDB> getQuestionRelationDBs() {
        if (mQuestionRelationDBs == null) {
            this.mQuestionRelationDBs = new Select()
                    .from(QuestionRelationDB.class)
                    //// FIXME: 29/12/16 https://github
                    // .com/Raizlabs/DBFlow/blob/f0d9e1710205952815db027cb560dd8868f5af0b/usage2
                    // /Indexing.md
                    //.indexedBy(Constants.QUESTION_RELATION_QUESTION_IDX)
                    .where(QuestionRelationDB_Table.id_question_fk
                            .eq(this.getId_question()))
                    .queryList();
        }
        return this.mQuestionRelationDBs;
    }

    public List<QuestionOptionDB> getQuestionOption() {

        if (this.mQuestionOptionDBs == null) {
            this.mQuestionOptionDBs = new Select().from(QuestionOptionDB.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_OPTION_QUESTION_IDX)
                    .where(QuestionOptionDB_Table.id_question_fk.eq(
                            this.getId_question()))
                    .queryList();
        }
        return this.mQuestionOptionDBs;
    }

    public List<QuestionOptionDB> getQuestionOptionsOfTypeMatch() {

        List<QuestionOptionDB> matchedQuestionOptionDB = null;

        matchedQuestionOptionDB =
                new Select().from(QuestionOptionDB.class).as(questionOptionName)
                        .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                        .on(QuestionOptionDB_Table.id_match_fk.withTable(
                                questionOptionAlias)
                                .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                        .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(
                        questionRelationName)
                        .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)
                                .eq(QuestionRelationDB_Table.id_question_relation.withTable(
                                        questionRelationAlias)))
                        .where(QuestionOptionDB_Table.id_question_fk.withTable(
                                questionOptionAlias).eq(
                                this.getId_question()))
                        .and(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(
                                QuestionRelationDB.MATCH))
                        .queryList();

        return matchedQuestionOptionDB;
    }

    public List<MatchDB> getMatchDBs() {
        if (mMatchDBs == null) {

            mMatchDBs = new Select().from(MatchDB.class).as(matchName)
                    .join(QuestionOptionDB.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                    .on(MatchDB_Table.id_match.withTable(matchAlias)
                            .eq(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)))
                    .where(QuestionOptionDB_Table.id_question_fk.withTable(questionOptionAlias).eq(
                            this.getId_question())).queryList();
        }
        return mMatchDBs;
    }

    public List<QuestionDB> getChildren() {
        if (this.children == null) {

            //No mMatchDBs no children
            List<MatchDB> matchDBs = getMatchDBs();
            if (matchDBs.size() == 0) {
                this.children = new ArrayList<>();
                return this.children;
            }

            Iterator<MatchDB> matchesIterator = matchDBs.iterator();
            Condition.In in = MatchDB_Table.id_match.withTable(matchAlias)
                    .in(matchesIterator.next().getId_match());
            while (matchesIterator.hasNext()) {
                in.and(Long.toString(matchesIterator.next().getId_match()));
            }

            //Select mQuestionDB from questionrelation where operator=1 and id_match in (..)
            this.children = new Select().from(QuestionDB.class).as(questionName)
                    //QuestionDB + QuestioRelation
                    .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                    .on(QuestionDB_Table.id_question.withTable(questionAlias)
                            .eq(QuestionRelationDB_Table.id_question_fk.withTable(
                                    questionRelationAlias)))
                    //+MatchDB
                    .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                    .on(QuestionRelationDB_Table.id_question_relation.withTable(questionRelationAlias)
                            .eq(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)))
                    //Parent child relationship
                    .where(in)
                    //In clause
                    .and(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(
                            QuestionRelationDB.PARENT_CHILD)).queryList();
        }
        return this.children;
    }

    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "values")
    public List<ValueDB> getValues() {
        if (values == null) {
            values = new Select()
                    .from(ValueDB.class)
                    .where(ValueDB_Table.id_question_fk
                            .eq(this.getId_question())).queryList();
        }
        return values;
    }

    /**
     * Gets the value of this mQuestionDB in the current survey in session
     */
    public ValueDB getValueBySession() {
        SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);
        return this.getValueBySurvey(surveyDB);
    }

    public OptionDB findOptionByValue(String value) {

        AnswerDB answerDB = getAnswerDB();
        if (answerDB == null) {
            return null;
        }

        List<OptionDB> optionDBs = answerDB.getOptionDBs();

        for (OptionDB optionDB : optionDBs) {
            String optionCode = optionDB.getCode();

            if (optionCode == null) {
                continue;
            }

            if (optionCode.equals(value)) {
                return optionDB;
            }
        }

        return null;
    }

    /**
     * Gets the value of this mQuestionDB in the given Survey
     */
    public ValueDB getValueBySurvey(SurveyDB surveyDB) {
        if (surveyDB == null) {
            return null;
        }
        List<ValueDB> returnValueDBs = new Select().from(ValueDB.class)
                //// FIXME: 29/12/16
                //.indexedBy(Constants.VALUE_IDX)
                .where(ValueDB_Table.id_question_fk.eq(this.getId_question()))
                .and(ValueDB_Table.id_survey_fk.eq(surveyDB.getId_survey())).queryList();

        if (returnValueDBs.size() == 0) {
            return null;
        } else {
            return returnValueDBs.get(0);
        }
    }

    /**
     * Gets the mOptionDB of this mQuestionDB in the current survey in session
     */
    public OptionDB getOptionBySession() {
        return this.getOptionBySurvey(SurveyFragmentStrategy.getSessionSurveyByQuestion(this));
    }

    /**
     * Gets the mOptionDB of this mQuestionDB in the given survey
     */
    public OptionDB getOptionBySurvey(SurveyDB surveyDB) {
        if (surveyDB == null) {
            return null;
        }

        ValueDB valueDB = this.getValueBySurvey(surveyDB);
        if (valueDB == null) {
            return null;
        }

        return valueDB.getOptionDB();
    }

    /**
     * Finds the next mQuestionDB to this one according to the order pos and considering children
     * mQuestionDBs too
     */
    public QuestionDB getSibling() {

        //Already calculated
        if (this.sibling != null) {
            Log.d(TAG, String.format("'%s'.getSibling() --cached--> '%s'", this.getCode(),
                    this.sibling.getCode()));
            return this.sibling;
        }

        //No parent -> just look for first mQuestionDB with upper order
        if (!this.hasParent()) {
            getSiblingNoParent();
        } else {
            getSiblingWithParent();
        }

        //Child mQuestionDB -> find next children mQuestionDB for same parent
        if (this.sibling != null && this.sibling.getCode() != null && this.getCode() != null) {
            Log.d(TAG, String.format("'%s'.getSibling() --calculated--> '%s'", this.getCode(),
                    this.sibling.getCode()));
        }
        return this.sibling.isNullQuestion() ? null : this.sibling;
    }

    private QuestionDB getSiblingWithParent() {
        //Find parent mQuestionDB
        QuestionOptionDB parentQuestionOptionDB = findParent();
        if (parentQuestionOptionDB == null) {
            this.sibling = buildNullQuestion();
            return this.sibling;
        }
        //Find children from parent
        List<QuestionDB> siblings = parentQuestionOptionDB.getQuestionDB().findChildrenByOption(
                parentQuestionOptionDB.getOptionDB());

        //Find current position of this
        int currentPosition = -1;
        for (int i = 0; i < siblings.size(); i++) {
            QuestionDB iQuestionDB = siblings.get(i);
            if (iQuestionDB.getId_question().equals(this.getId_question())) {
                currentPosition = i;
                break;
            }
        }

        //Last children mQuestionDB
        if (currentPosition == siblings.size() - 1) {
            this.sibling = buildNullQuestion();
            return this.sibling;
        }
        //Return next position
        this.sibling = siblings.get(currentPosition + 1);
        return this.sibling;
    }

    /**
     * Returns next mQuestionDB from same mHeaderDB considering the order.
     * This should not be a child mQuestionDB.
     */
    private QuestionDB getSiblingNoParent() {

        //Take every child mQuestionDB
        List<QuestionRelationDB> questionRelationDBs = QuestionRelationDB.listAllParentChildRelations();
        //Build a not in condition
        Condition.In in;
        if (questionRelationDBs.size() == 0) {
            //Flow without children
            in = QuestionDB_Table.id_question.withTable(questionAlias).notIn(
                    this.getId_question());
        } else {
            Iterator<QuestionRelationDB> questionRelationsIterator = questionRelationDBs.iterator();
            in = QuestionDB_Table.id_question.withTable(questionAlias).notIn(
                    questionRelationsIterator.next().getQuestionDB().getId_question());
            while (questionRelationsIterator.hasNext()) {
                in.and(Long.toString(
                        questionRelationsIterator.next().getQuestionDB().getId_question()));
            }
        }

        ProgramDB questionProgramDB = getQuestionProgram();

        //Siblings without parents relations
        List<QuestionDB> questionDBs = new Select().from(QuestionDB.class).as(questionName)
                .where(QuestionDB_Table.order_pos.withTable(questionAlias)
                        .greaterThan(this.getOrder_pos()))
                .and(in)
                .orderBy(OrderBy.fromProperty(QuestionDB_Table.order_pos).ascending()).queryList();

        //Doing like this because DBFLOW bug
        for (QuestionDB questionDB : questionDBs) {
            if (questionDB.getQuestionProgram().equals(questionProgramDB)) {
                this.sibling = questionDB;
                break;
            }
        }

        //no mQuestionDB behind this one -> build a null mQuestionDB to use cached value
        if (this.sibling == null) {
            this.sibling = buildNullQuestion();
        }
        return this.sibling;
    }

    private ProgramDB getQuestionProgram() {

        return new Select().from(ProgramDB.class).as(programName)
                .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(TabDB_Table.id_program_fk.withTable(tabAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .where(QuestionDB_Table.id_question.withTable(questionAlias).eq(
                        this.getId_question())).querySingle();
    }

    public static List<QuestionDB> getQuestionsByTab(TabDB tabDB) {
        //Select mQuestionDB from questionrelation where operator=1 and id_match in (..)
        return new Select().from(QuestionDB.class).as(questionName)
                //QuestionDB + QuestioRelation
                .join(HeaderDB.class, Join.JoinType.LEFT_OUTER).as(headerName)
                .on(QuestionDB_Table.id_header_fk.withTable(questionAlias)
                        .eq(HeaderDB_Table.id_header.withTable(headerAlias)))
                .join(TabDB.class, Join.JoinType.LEFT_OUTER).as(tabName)
                .on(HeaderDB_Table.id_tab_fk.withTable(headerAlias)
                        .eq(TabDB_Table.id_tab.withTable(tabAlias)))
                .where(TabDB_Table.id_tab.withTable(tabAlias).eq(
                        tabDB.getId_tab()))
                .orderBy(OrderBy.fromProperty(QuestionDB_Table.order_pos).ascending())
                .queryList();
    }

    public List<QuestionThresholdDB> getQuestionThresholdDBs() {

        if (this.mQuestionThresholdDBs == null) {
            this.mQuestionThresholdDBs = new Select().from(QuestionThresholdDB.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_THRESHOLDS_QUESTION_IDX)
                    .where(QuestionThresholdDB_Table.id_question_fk.eq(
                            this.getId_question()))
                    .queryList();
        }
        return this.mQuestionThresholdDBs;
    }

    public OptionDB getAnsweredOption() {
        SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);

        ValueDB valueDB = ValueDB.findValue(getId_question(), surveyDB);
        if (valueDB != null) {
            return OptionDB.findById(valueDB.getId_option());
        }
        return null;
    }

    public OptionDB getOptionByValueInSession() {
        OptionDB optionDB = null;
        ValueDB valueDB = getValueBySession();

        if (valueDB != null) {
            optionDB = valueDB.getOptionDB();
        }
        return optionDB;
    }

    private Context getContext() {
        return PreferencesState.getInstance().getContext();
    }

    public String getQuestionValueBySession() {
        String result = null;

        ValueDB valueDB = getValueBySession();

        if (valueDB != null) {
            result = valueDB.getValue();
        }

        return result;
    }

    /**
     * Add register to ScoreRegister if this is an scored mQuestionDB
     *
     * @return List</Float> {num, den}
     */
    public List<Float> initScore(SurveyDB surveyDB) {
        if (!this.isScored()) {
            return null;
        }

        Float num = ScoreRegister.calcNum(this, surveyDB);
        Float denum = ScoreRegister.calcDenum(this, surveyDB);
        ScoreRegister.addRecord(this, num, denum);
        return Arrays.asList(num, denum);
    }

    public void saveValuesDDL(OptionDB optionDB, ValueDB valueDB) {
        //No mOptionDB, nothing to save
        if (optionDB == null) {
            return;
        }
        SurveyFragmentStrategy.saveValueDDlExtraOperations(valueDB, optionDB, getUid());

        if (!optionDB.getCode().equals(Constants.DEFAULT_SELECT_OPTION)) {
            SurveyDB surveyDB = SurveyFragmentStrategy.getSaveValuesDDLSurvey(this);

            createOrSaveDDLValue(optionDB, valueDB, surveyDB);
            for (QuestionDB propagateQuestionDB : this.getPropagationQuestions()) {
                propagateQuestionDB.createOrSaveDDLValue(optionDB,
                        ValueDB.findValue(propagateQuestionDB.getId_question(),
                                Session.getMalariaSurveyDB()), Session.getMalariaSurveyDB());
            }
        } else {
            deleteValues(valueDB);
        }
    }

    public void saveValuesText(String answer) {
        ValueDB valueDB = getValueBySession();
        SurveyDB surveyDB = (SurveyFragmentStrategy.getSessionSurveyByQuestion(this));
        SurveyFragmentStrategy.saveValuesText(valueDB, answer, this, surveyDB);

    }

    public void deleteValues(ValueDB valueDB) {
        if (valueDB != null) {
            for (QuestionDB propagateQuestionDB : this.getPropagationQuestions()) {
                ValueDB propagateValueDB = ValueDB.findValueFromDatabase(
                        propagateQuestionDB.getId_question(),
                        Session.getMalariaSurveyDB());
                if (propagateValueDB != null) {
                    propagateValueDB.delete();
                }
            }
            valueDB.delete();
        }
    }

    private void createOrSaveDDLValue(OptionDB optionDB, ValueDB valueDB,
            SurveyDB surveyDB) {
        if (valueDB == null) {
            valueDB = new ValueDB(optionDB, this, surveyDB);
        } else {
            SurveyFragmentStrategy.recursiveRemover(valueDB, optionDB, this, surveyDB);
            valueDB.setOptionDB(optionDB);
            valueDB.setValue(optionDB.getCode());
        }

        valueDB.save();
    }

    public void createOrSaveValue(String answer, ValueDB valueDB,
            SurveyDB surveyDB) {
        // If the valueDB is not found we create one
        if (valueDB == null) {
            valueDB = new ValueDB(answer, this, surveyDB);
        } else {
            valueDB.setOption((Long) null);
            valueDB.setValue(answer);
        }
        valueDB.save();
    }

    public void deleteValueBySession() {
        ValueDB valueDB = getValueBySession();

        if (valueDB != null) {
            deleteValues(valueDB);
        }
    }

    /*Returns true if the mQuestionDB belongs to a Custom TabDB*/
    public boolean belongsToCustomTab() {

        return getHeaderDB().getTabDB().isACustomTab();
    }

    /**
     * Checks if this mQuestionDB is shown according to the values of the given survey
     */
    public boolean isHiddenBySurvey(long idSurvey) {
        //No mQuestionDB relations
        if (!hasParent()) {
            return false;
        }
        long hasParentOptionActivated = SQLite.selectCountOf().from(ValueDB.class).as(valueName)
                .join(QuestionOptionDB.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        ValueDB_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelationDB_Table.id_question_relation))
                //Parent child relationship
                .where(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(1))
                //For the given survey
                .and(ValueDB_Table.id_survey_fk.withTable(valueAlias).eq(
                        idSurvey))
                //The child mQuestionDB in the relationship is 'this'
                .and(QuestionRelationDB_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                .count();

        //Parent with the right value -> not hidden
        return hasParentOptionActivated > 0 ? false : true;
    }

    /**
     * Checks if this mQuestionDB is shown according to the values of the given survey
     */
    public boolean isHiddenBySurveyAndHeader(SurveyDB surveyDB) {
        if (surveyDB == null) {
            return false;
        }
        //No mQuestionDB relations
        if (!hasParentInSameHeader()) {
            return false;
        }
        long hasParentOptionActivated = SQLite.selectCountOf().from(ValueDB.class).as(valueName)
                .join(QuestionOptionDB.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        ValueDB_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)
                        .eq(QuestionRelationDB_Table.id_question_relation.withTable(
                                questionRelationAlias)))
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(QuestionDB_Table.id_question.withTable(questionAlias)
                        .eq(QuestionOptionDB_Table.id_question_fk.withTable(questionOptionAlias)))
                //Parent child relationship
                .where(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(1))
                //For the given survey
                .and(ValueDB_Table.id_survey_fk.withTable(valueAlias).eq(
                        surveyDB.getId_survey()))
                //The child mQuestionDB in the relationship is 'this'
                .and(QuestionRelationDB_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                //Group parents by mHeaderDB
                .and(QuestionDB_Table.id_header_fk.withTable(questionAlias).eq(
                        this.getHeaderDB().getId_header()))
                .count();
        //Parent with the right value -> not hidden
        return hasParentOptionActivated > 0 ? false : true;
    }

    public boolean hasQuestionOption() {
        return !this.getQuestionOption().isEmpty();
    }

    public boolean hasQuestionRelations() {
        return !this.getQuestionRelationDBs().isEmpty();
    }

    /**
     * Tells if this mQuestionDB has mOptionDBs or is an open value
     */
    public boolean hasOutputWithOptions() {
        return Constants.QUESTION_TYPES_WITH_OPTIONS.contains(this.output);

    }

    public boolean hasParent() {
        if (parent == null) {
            long countChildQuestionRelations = SQLite.selectCountOf().from(
                    QuestionRelationDB.class)
                    //// FIXME: 29/12/16
                    //.indexedBy(Constants.QUESTION_RELATION_QUESTION_IDX)
                    .where(QuestionRelationDB_Table.id_question_fk.eq(
                            this.getId_question()))
                    .and(QuestionRelationDB_Table.operation.eq(
                            QuestionRelationDB.PARENT_CHILD))
                    .count();
            parent = Boolean.valueOf(countChildQuestionRelations > 0);
        }
        return parent;
    }

    public boolean hasParentInSameHeader() {
        //FIXME: this method is by hand doing something that might be done with a DB query
        if (parentHeader == null) {
            parentHeader = false;

            List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithHeader(getHeaderDB());
            //Only one its itself.
            if (questionDBs == null || questionDBs.size() <= 1) {
                return false;
            }

            //Removes itself
            questionDBs.remove(this);

            for (QuestionDB questionDB : questionDBs) {
                for (QuestionOptionDB questionOptionDB : questionDB.getQuestionOption()) {
                    MatchDB matchDB = questionOptionDB.getMatchDB();
                    QuestionRelationDB questionRelationDB = matchDB.getQuestionRelationDB();
                    if (questionRelationDB.getOperation() == QuestionRelationDB.PARENT_CHILD
                            && questionRelationDB.getQuestionDB().getId_question().equals(
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
        return !this.getQuestionThresholdDBs().isEmpty();
    }

    /**
     * Checks if this mQuestionDB is triggered according to the current values of the given survey.
     * Only applies to mQuestionDB with answers DROPDOWN_DISABLED
     */
    public boolean isTriggered(float idSurvey) {

        //Only disabled dropdowns
        if (this.getOutput() != Constants.DROPDOWN_LIST_DISABLED) {
            return false;
        }

        //Find questionoptions for q1 and q2 and check same mMatchDB
        List<QuestionOptionDB> questionOptionDBs = new Select().from(QuestionOptionDB.class).as(
                questionOptionName)
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias).eq(
                        MatchDB_Table.id_match.withTable(matchAlias)))
                .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(MatchDB_Table.id_question_relation_fk.withTable(matchAlias).eq(
                        QuestionRelationDB_Table.id_question_relation))

                .join(ValueDB.class, Join.JoinType.LEFT_OUTER).as(valueName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_question_fk.withTable
                                        (questionOptionAlias)),
                        ValueDB_Table.id_option_fk.withTable(valueAlias)
                                .eq(QuestionOptionDB_Table.id_option_fk.withTable(
                                        questionOptionAlias)))
                .where(ValueDB_Table.id_survey_fk.withTable(valueAlias).eq((long) idSurvey))
                .and(QuestionRelationDB_Table.id_question_fk.withTable(questionRelationAlias).eq(
                        this.getId_question()))
                .and(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelationDB.MATCH))
                .queryList();

        //No values no mMatchDB
        if (questionOptionDBs.size() != 2) {
            return false;
        }

        //MatchDB is triggered if questionoptions have same matchid
        long idmatchQ1 = questionOptionDBs.get(0).getMatchDB().getId_match();
        long idmatchQ2 = questionOptionDBs.get(1).getMatchDB().getId_match();
        return idmatchQ1 == idmatchQ2;

    }

    /**
     * Checks if this mQuestionDB is scored or not.
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
     * Find the first children mQuestionDB for this mQuestionDB taking into the account the given
     * mOptionDB
     */
    public QuestionDB findFirstChildrenByOption(OptionDB optionDB) {
        List<QuestionDB> childrenQuestionDBs = findChildrenByOption(optionDB);
        if (childrenQuestionDBs == null || childrenQuestionDBs.size() == 0) {
            return null;
        }

        return childrenQuestionDBs.get(0);
    }

    /**
     * Find the children mQuestionDBs for this mQuestionDB taking into the account the given
     * mOptionDB
     */
    public List<QuestionDB> findChildrenByOption(OptionDB optionDB) {
        List<QuestionDB> childrenQuestionDBs = new ArrayList<>();
        //No mOptionDB -> no children
        if (optionDB == null) {
            return childrenQuestionDBs;
        }

        List<QuestionOptionDB> questionOptionDBs = this.getQuestionOption();
        //No trigger (questionOption) -> no children
        if (questionOptionDBs == null || questionOptionDBs.size() == 0) {
            return childrenQuestionDBs;
        }

        //Navigate to mQuestionRelationDB to get child mQuestionDBs
        long optionId = optionDB.getId_option().longValue();
        for (QuestionOptionDB questionOptionDB : questionOptionDBs) {
            //Other mOptionDBs must be discarded
            if (discardOptions(optionId, questionOptionDB)) continue;
            MatchDB matchDB = questionOptionDB.getMatchDB();
            if (matchDB == null) {
                continue;
            }

            QuestionRelationDB questionRelationDB = matchDB.getQuestionRelationDB();
            //only parent child are interesting for this
            if (questionRelationDB == null
                    || questionRelationDB.getOperation() != QuestionRelationDB.PARENT_CHILD) {
                continue;
            }

            QuestionDB childQuestionDB = questionRelationDB.getQuestionDB();
            if (childQuestionDB == null) {
                continue;
            }
            childrenQuestionDBs.add(childQuestionDB);
        }

        //Sort asc by order pos
        Collections.sort(childrenQuestionDBs, new QuestionOrderComparator());

        return childrenQuestionDBs;
    }

    private boolean discardOptions(long optionId, QuestionOptionDB questionOptionDB) {
        if (questionOptionDB.getOptionDB() == null) {
            return true;
        }
        long currentOptionId = questionOptionDB.getOptionDB().getId_option().longValue();
        if (optionId != currentOptionId) {
            return true;
        }
        return false;
    }

    public ValueDB insertValue(String value, SurveyDB surveyDB) {
        return new ValueDB(value, this, surveyDB);
    }

    /**
     * Find the counter mQuestionDB for this mQuestionDB taking into the account the given
     * mOptionDB.
     * Only 1 counter mQuestionDB will be activated by mOptionDB
     */
    public QuestionDB findCounterByOption(OptionDB optionDB) {

        //No mOptionDB -> no children
        if (optionDB == null) {
            return null;
        }

        List<QuestionOptionDB> questionOptionDBs = this.getQuestionOption();
        //No trigger (questionOption) -> no counters
        if (questionOptionDBs == null || questionOptionDBs.size() == 0) {
            return null;
        }

        //Navigate to mQuestionRelationDB to get child mQuestionDBs
        long optionId = optionDB.getId_option().longValue();
        for (QuestionOptionDB questionOptionDB : questionOptionDBs) {
            //Other mOptionDBs must be discarded
            if (discardOptions(optionId, questionOptionDB)) continue;
            MatchDB matchDB = questionOptionDB.getMatchDB();
            if (matchDB == null) {
                continue;
            }

            QuestionRelationDB questionRelationDB = matchDB.getQuestionRelationDB();
            //only COUNTER RELATIONSHIPs are interesting for this
            if (questionRelationDB == null
                    || questionRelationDB.getOperation() != QuestionRelationDB.COUNTER) {
                continue;
            }

            QuestionDB childQuestionDB = questionRelationDB.getQuestionDB();
            if (childQuestionDB == null) {
                continue;
            }

            //Found
            return childQuestionDB;
        }

        return null;
    }

    /**
     * Returns a list of mQuestionOptionDBs that activates this mQuestionDB (might be several
     * though it
     * tends to be just one)
     */
    public List<QuestionOptionDB> findParents() {
        List<QuestionOptionDB> parents = new ArrayList<>();
        //No potential parents
        if (!this.hasParent()) {
            return parents;
        }

        //Add parents via (questionrelation->mMatchDB->questionoption->mQuestionDB
        List<QuestionRelationDB> questionRelationDBs = this.getQuestionRelationDBs();
        for (QuestionRelationDB questionRelationDB : questionRelationDBs) {
            //Only parentchild relationships
            if (questionRelationDB.getOperation() != QuestionRelationDB.PARENT_CHILD) {
                continue;
            }
            for (MatchDB matchDB : questionRelationDB.getMatchDBs()) {
                parents.addAll(matchDB.getQuestionOptionDBs());
            }
        }

        return parents;
    }

    /**
     * Returns the first parent candidate.
     * XXX: This is a shortcut considering that there wont be more than parent but this is not
     * guaranteed
     */
    public QuestionOptionDB findParent() {

        List<QuestionOptionDB> parents = findParents();
        if (parents == null || parents.size() == 0) {
            return null;
        }
        return parents.get(0);
    }

    public boolean isAnswered() {
        return (this.getValueBySession() != null);
    }

    public boolean isNotAnswered(QuestionDB questionDB) {
        if (questionDB.getValueBySession() == null
                || questionDB.getValueBySession().getValue() == null
                || questionDB.getValueBySession().getValue().length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Returns if the mQuestionDB is a counter or not
     */
    public boolean isACounter() {
        QuestionRelationDB questionRelationDB = new Select().from(QuestionRelationDB.class).where(
                QuestionRelationDB_Table.operation.eq(
                        QuestionRelationDB.COUNTER)).and(
                QuestionRelationDB_Table.id_question_fk.eq(
                        this.getId_question())).querySingle();
        return questionRelationDB != null;
    }


    public boolean hasCompulsoryNotAnswered() {
        //get all the mQuestionDBs in the same screen page

        List<QuestionDB> questionDBs = SurveyFragmentStrategy.getCompulsoryNotAnsweredQuestions(
                this);
        if (questionDBs.size() == 0) {
            return true;
        }
        for (QuestionDB questionDB : questionDBs) {
            SurveyDB surveyDB = SurveyFragmentStrategy.getSessionSurveyByQuestion(this);
            if (questionDB.isCompulsory() && !questionDB.isHiddenBySurveyAndHeader(
                    surveyDB) && isNotAnswered(questionDB)) {
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

        QuestionDB questionDB = (QuestionDB) o;

        if (id_question != questionDB.id_question) return false;
        if (code != null ? !code.equals(questionDB.code) : questionDB.code != null) return false;
        if (de_name != null ? !de_name.equals(questionDB.de_name) : questionDB.de_name != null) {
            return false;
        }
        if (help_text != null ? !help_text.equals(questionDB.help_text)
                : questionDB.help_text != null) {
            return false;
        }
        if (form_name != null ? !form_name.equals(questionDB.form_name)
                : questionDB.form_name != null) {
            return false;
        }
        if (uid_question != null ? !uid_question.equals(questionDB.uid_question)
                : questionDB.uid_question != null) {
            return false;
        }
        if (order_pos != null ? !order_pos.equals(questionDB.order_pos)
                : questionDB.order_pos != null) {
            return false;
        }
        if (numerator_w != null ? !numerator_w.equals(questionDB.numerator_w)
                : questionDB.numerator_w != null) {
            return false;
        }
        if (denominator_w != null ? !denominator_w.equals(questionDB.denominator_w)
                : questionDB.denominator_w != null) {
            return false;
        }
        if (feedback != null ? !feedback.equals(questionDB.feedback)
                : questionDB.feedback != null) {
            return false;
        }
        if (id_header_fk != null ? !id_header_fk.equals(questionDB.id_header_fk)
                : questionDB.id_header_fk != null) {
            return false;
        }
        if (id_answer_fk != null ? !id_answer_fk.equals(questionDB.id_answer_fk)
                : questionDB.id_answer_fk != null) {
            return false;
        }
        if (output != null ? !output.equals(questionDB.output) : questionDB.output != null) {
            return false;
        }
        if (id_question_parent != null ? !id_question_parent.equals(questionDB.id_question_parent)
                : questionDB.id_question_parent != null) {
            return false;
        }
        if (path != null ? !path.equals(questionDB.path) : questionDB.path != null) {
            return false;
        }
        if (total_questions != null ? !total_questions.equals(questionDB.total_questions)
                : questionDB.total_questions != null) {
            return false;
        }
        if (visible != null ? !visible.equals(questionDB.visible) : questionDB.visible != null) {
            return false;
        }
        if (compulsory != null ? !compulsory.equals(questionDB.compulsory)
                : questionDB.compulsory != null) {
            return false;
        }
        return !(id_composite_score_fk != null ? !id_composite_score_fk.equals(
                questionDB.id_composite_score_fk) : questionDB.id_composite_score_fk != null);

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

    public List<QuestionDB> getPropagationQuestions() {
        if (mPropagationQuestionDB != null) {
            return mPropagationQuestionDB;
        }
        //No mMatchDBs no children
        List<MatchDB> matchDBs = getMatchDBs();
        if (matchDBs.size() == 0) {
            this.mPropagationQuestionDB = new ArrayList<>();
            return this.mPropagationQuestionDB;
        }
        //Select mQuestionDB from questionrelation where operator=1 and id_match in (..)
        mPropagationQuestionDB = new Select().from(QuestionDB.class).as(questionName)
                //QuestionDB + QuestioRelation
                .join(QuestionRelationDB.class, Join.JoinType.LEFT_OUTER).as(questionRelationName)
                .on(QuestionDB_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelationDB_Table.id_question_fk.withTable(
                                questionRelationAlias)))
                //+MatchDB
                .join(MatchDB.class, Join.JoinType.LEFT_OUTER).as(matchName)
                .on(QuestionRelationDB_Table.id_question_relation.withTable(questionRelationAlias)
                        .eq(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)))
                //+Questionoption
                .join(QuestionOptionDB.class, Join.JoinType.LEFT_OUTER).as(questionOptionName)
                .on(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)
                        .eq(MatchDB_Table.id_match.withTable(matchAlias)))
                //Parent child relationship
                .where(QuestionRelationDB_Table.operation.withTable(questionRelationAlias).eq(
                        QuestionRelationDB.MATCH_PROPAGATE))
                .and(QuestionOptionDB_Table.id_question_fk.withTable(questionOptionAlias).is(
                        id_question)).queryList();
        return mPropagationQuestionDB;
    }

    private static class QuestionOrderComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {

            QuestionDB questionDB1 = (QuestionDB) o1;
            QuestionDB questionDB2 = (QuestionDB) o2;

            return new Integer(
                    questionDB1.getOrder_pos().compareTo(new Integer(questionDB2.getOrder_pos())));
        }
    }
}