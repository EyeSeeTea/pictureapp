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

package org.eyeseetea.malariacare.database.model;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.IConvertToSDKVisitor;
import org.eyeseetea.malariacare.database.iomodules.dhis.exporter.VisitableToSDK;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.database.utils.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.database.utils.SurveyAnsweredRatioCache;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Table(databaseName = AppDatabase.NAME)
public class Survey extends BaseModel implements VisitableToSDK {
    public static final float MAX_AMBER = 80f;
    public static final float MAX_RED = 50f;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_survey;

    @Column
    Long id_program;
    /**
     * Reference to the program associated to this survey (loaded lazily)
     */
    Program program;

    @Column
    Long id_org_unit;
    /**
     * Reference to the org unit associated to this survey (loaded lazily)
     */
    OrgUnit orgUnit;

    @Column
    Long id_user;
    /**
     * Reference to the user that has created this survey (loaded lazily)
     */
    User user;

    @Column
    Date creationDate;

    @Column
    Date completionDate;

    @Column
    Date eventDate;

    @Column
    Date scheduledDate;

    @Column
    Integer status;

    /**
     * List of values for this survey
     */
    List<Value> values;

    /**
     * List of historic previous schedules
     */
    List<SurveySchedule> surveySchedules;

    /**
     * Calculated answered ratio for this survey according to its values
     */
    SurveyAnsweredRatio answeredQuestionRatio;

    /**
     * Calculated main Score for this survey, is not persisted, just calculated on runtime
     */
    Float mainScore;

    public Survey() {
        //Set dates
        this.creationDate = new Date();
        this.completionDate = this.creationDate;
        this.eventDate = new Date();
        this.scheduledDate = null;
    }

    public Survey(OrgUnit orgUnit, Program program, User user) {
        this();

        // Possibilities [ In progress | Completed | Sent ]
        this.status = Constants.SURVEY_IN_PROGRESS;

        //Set context of the survey
        this.setOrgUnit(orgUnit);
        this.setProgram(program);
        this.setUser(user);
    }

    /**
     * Returns a concrete survey, if it exists
     */
    public static List<Survey> getUnsentSurveys(OrgUnit orgUnit, Program program) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.ID_ORG_UNIT).eq(orgUnit.getId_org_unit()))
                .and(Condition.column(Survey$Table.ID_PROGRAM).eq(program.getId_program()))
                .and(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the malaria surveys with status yet not put to "Sent"
     */
    public static List<Survey> getAllUnsentMalariaSurveys() {
        return new Select().from(Survey.class).as("s")
                .join(Program.class, Join.JoinType.LEFT).as("p")
                .on(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.ID_PROGRAM))
                        .eq(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM)))
                .where(Condition.column(
                        ColumnAlias.columnWithTable("s", Survey$Table.STATUS)).isNot(
                        Constants.SURVEY_SENT))
                .and(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.STATUS)).isNot(
                        Constants.SURVEY_CONFLICT))
                .and(Condition.column(ColumnAlias.columnWithTable("p", Program$Table.UID)).isNot(
                        "GnAx3VLVfUi"))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns the last surveys (by date) with status yet not put to "Sent"
     */
    public static List<Survey> getUnsentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .limit(String.valueOf(limit))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the malaria surveys with status put to "Sent"
     */
    public static List<Survey> getAllSentMalariaSurveys() {
        return new Select().from(Survey.class).as("s")
                .join(Program.class, Join.JoinType.LEFT).as("p")
                .on(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.ID_PROGRAM))
                        .eq(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM)))
                .where(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.STATUS)).eq(
                        Constants.SURVEY_SENT))
                .and(Condition.column(ColumnAlias.columnWithTable("p", Program$Table.UID)).isNot(
                        "GnAx3VLVfUi"))
                .orderBy(false, Survey$Table.EVENTDATE).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static List<Survey> getAllQuarantineSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_QUARANTINE))
                .orderBy(false, Survey$Table.EVENTDATE).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static int countQuarantineSurveys() {
        return (int) new Select().count()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_QUARANTINE))
                .count();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<Survey> getAllMalariaSurveysToBeSent() {
        return new Select().from(Survey.class).as("s")
                .join(Program.class, Join.JoinType.LEFT).as("p")
                .on(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.ID_PROGRAM))
                        .eq(ColumnAlias.columnWithTable("p", Program$Table.ID_PROGRAM)))
                .where(Condition.column(ColumnAlias.columnWithTable("s", Survey$Table.STATUS)).eq(
                        Constants.SURVEY_COMPLETED))
                .and(Condition.column(ColumnAlias.columnWithTable("p", Program$Table.UID)).isNot(
                        "GnAx3VLVfUi"))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the surveys
     */
    public static List<Survey> getAllSurveys() {
        return new Select().all().from(Survey.class)
                .orderBy(false, Survey$Table.COMPLETIONDATE).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Sent"
     */
    public static List<Survey> getSentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .limit(String.valueOf(limit))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the surveys with status put to "Completed"
     */
    public static List<Survey> getAllCompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_COMPLETED))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Completed"
     */
    public static List<Survey> getCompletedSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_COMPLETED))
                .limit(String.valueOf(limit))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the surveys with status put to "In progress"
     */
    public static List<Survey> getAllUncompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).is(Constants.SURVEY_IN_PROGRESS))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "In progress"
     */
    public static List<Survey> getUncompletedSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_IN_PROGRESS))
                .limit(String.valueOf(limit))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<Survey> getAllUncompletedUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_COMPLETED))
                .or(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .or(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<Survey> getAllCompletedUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).is(Constants.SURVEY_COMPLETED))
                .or(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .or(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent" or completed
     */
    public static List<Survey> getAllSentOrCompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .or(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_COMPLETED))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT).queryList();
    }

    // Returns all the surveys with status put to "Hide"
    public static List<Survey> getAllHideAndSentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .or(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ID_ORG_UNIT)
                .queryList();
    }

    public static void removeInProgress() {
        List<Survey> inProgressSurvey = getAllUncompletedSurveys();
        for (int i = inProgressSurvey.size() - 1; i >= 0; i--) {
            inProgressSurvey.get(i).delete();
        }
    }

    /**
     * Find the surveys that have been sent after the given date
     */
    public static List<Survey> findSentSurveysAfterDate(Date minDateForMonitor) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .and(Condition.column(Survey$Table.EVENTDATE).greaterThanOrEq(
                        minDateForMonitor)).queryList();
    }

    /**
     * Finds a survey by its ID
     */
    public static Survey findById(Long id_survey) {
        return new Select()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.ID_SURVEY)
                        .eq(id_survey))
                .querySingle();
    }

    private static void removeValue(Value value) {
        value.delete();
    }

    public static int countSurveysByCompletiondate(Date completionDate) {

        return (int) new Select().count()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.COMPLETIONDATE).eq(completionDate))
                .count();
    }

    public static Date getMinQuarantineEventDate() {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_QUARANTINE))
                .orderBy(true, Survey$Table.EVENTDATE)
                .querySingle();
        return survey.getEventDate();
    }

    public static Date getMaxQuarantineEventDate() {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_QUARANTINE))
                .orderBy(false, Survey$Table.EVENTDATE)
                .querySingle();
        return survey.getEventDate();
    }

    public static List<Survey> getAllSendingSurveys() {
        return new Select()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENDING))
                .queryList();
    }

    public Long getId_survey() {
        return id_survey;
    }

    public void setId_survey(Long id_survey) {
        this.id_survey = id_survey;
    }

    public OrgUnit getOrgUnit() {
        if (orgUnit == null) {
            if (id_org_unit == null) return null;
            orgUnit = new Select()
                    .from(OrgUnit.class)
                    .where(Condition.column(OrgUnit$Table.ID_ORG_UNIT)
                            .is(id_org_unit)).querySingle();
        }
        return orgUnit;
    }

    public void setOrgUnit(Long id_org_unit) {
        this.id_org_unit = id_org_unit;
        this.orgUnit = null;
    }

    public void setOrgUnit(OrgUnit orgUnit) {
        this.orgUnit = orgUnit;
        this.id_org_unit = (orgUnit != null) ? orgUnit.getId_org_unit() : null;
    }

    public Program getProgram() {
        if (program == null) {
            if (id_program == null) return null;
            program = new Select()
                    .from(Program.class)
                    .where(Condition.column(Program$Table.ID_PROGRAM)
                            .is(id_program)).querySingle();
        }
        return program;
    }

    public void setProgram(Long id_program) {
        this.id_program = id_program;
        this.program = null;
    }

    public void setProgram(Program program) {
        this.program = program;
        this.id_program = (program != null) ? program.getId_program() : null;
    }

    public User getUser() {
        if (user == null) {
            if (id_user == null) return null;
            user = new Select()
                    .from(User.class)
                    .where(Condition.column(User$Table.ID_USER)
                            .is(id_user)).querySingle();
        }
        return user;
    }

    public void setUser(Long id_user) {
        this.id_user = id_user;
        this.user = null;
    }

    public void setUser(User user) {
        this.user = user;
        this.id_user = (user != null) ? user.getId_user() : null;
    }

    @Deprecated
    public Date getCreationDate() {
        return creationDate;
    }

    @Deprecated
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Deprecated
    public Date getEventDate() {
        return eventDate;
    }

    @Deprecated
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Checks if the survey has been sent or not
     *
     * @return true|false
     */
    public boolean isSent() {
        return Constants.SURVEY_SENT == this.status;
    }

    /**
     * Checks if the survey has been hidden or not
     *
     * @return true|false
     */
    public boolean isHide() {
        return Constants.SURVEY_HIDE == this.status;
    }

    /**
     * Checks if the survey has been completed or not
     *
     * @return true|false
     */
    public boolean isCompleted() {
        return Constants.SURVEY_COMPLETED == this.status;
    }

    /**
     * Checks if the survey has been in conflict
     *
     * @return true|false
     */
    public boolean isConflict() {
        return Constants.SURVEY_CONFLICT == this.status;
    }

    /**
     * Checks if the survey has been in conflict
     *
     * @return true|false
     */
    public boolean isQuarantine() {
        return Constants.SURVEY_QUARANTINE == this.status;
    }

    /**
     * Checks if the survey has been completed or not
     *
     * @return true|false
     */
    public boolean isCompleted(Long idSurvey) {
        Survey srv = new Select()
                .from(Survey.class)
                .where(Condition.column(Survey$Table.ID_SURVEY).eq(idSurvey)).querySingle();

        return srv.getStatus().equals(Constants.SURVEY_COMPLETED);
    }

    /**
     * Checks if the survey is in progress
     *
     * @return true|false
     */
    public boolean isInProgress() {
        return !isSent() && !isCompleted() && !isHide();
    }

    public boolean isStockSurvey() {
        if (program == null) {
            program = Program.findById(id_program);
        }
        if (program != null) {
            return program.isStockProgram();
        }
        return false;
    }

    public Float getMainScore() {
        //The main score is only return from a query 1 time
        if (this.mainScore == null) {
            Score score = getScore();
            this.mainScore = (score == null) ? 0f : score.getScore();
        }
        return mainScore;
    }


    public void setMainScore(Float mainScore) {
        this.mainScore = mainScore;
    }

    public void saveMainScore() {
        Float valScore = 0f;
        if (mainScore != null) {
            valScore = mainScore;
        }
        Score score = new Score(this, "", valScore);
        score.save();
    }

    private Score getScore() {
        return new Select()
                .from(Score.class)
                .where(Condition.column(Score$Table.ID_SURVEY).eq(
                        this.getId_survey())).querySingle();
    }

    @Override
    public void delete() {
        Score score = getScore();
        if (score != null) {
            score.delete();
        }
        for (Value value : getValues()) {
            value.delete();
        }
        super.delete();
    }

    public String getType() {
        String type = "";
        if (isTypeA()) {
            type = "A";
        } else if (isTypeB()) {
            type = "B";
        } else if (isTypeC()) type = "C";
        return type;
    }

    /**
     * Returns this survey is type A (green)
     */
    public boolean isTypeA() {
        return this.mainScore >= MAX_AMBER;
    }

    /**
     * Returns this survey is type B (amber)
     */
    public boolean isTypeB() {
        return this.mainScore >= MAX_RED && !isTypeA();
    }

    /**
     * Returns this survey is type C (red)
     */
    public boolean isTypeC() {
        return !isTypeA() && !isTypeB();
    }

    /**
     * Returns the list of answered values from this survey
     */
    public List<Value> getValues() {
        if (values == null) {
            values = new Select()
                    .from(Value.class)
                    .where(Condition.column(Value$Table.ID_SURVEY)
                            .eq(this.getId_survey())).queryList();
        }
        return values;
    }

    /**
     * Returns the list of answered values from this survey
     */
    public List<Value> getValuesFromDB() {
        values = new Select()
                .from(Value.class)
                .where(Condition.column(Value$Table.ID_SURVEY)
                        .eq(this.getId_survey())).queryList();
        return values;
    }

    /**
     * Returns the list of questions from answered values
     */
    public List<Question> getQuestionsFromValues() {
        List<Question> questions = new Select()
                .from(Question.class).as("q")
                .join(Value.class, Join.JoinType.LEFT).as("v")
                .on(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_QUESTION))
                        .eq(ColumnAlias.columnWithTable("q", Question$Table.ID_QUESTION)))
                .where(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_SURVEY))
                        .eq(this.getId_survey()))
                .orderBy(true, Question$Table.ORDER_POS).queryList();
        return questions;
    }

    /**
     * Returns the list of previous schedules for this survey
     */
    public List<SurveySchedule> getSurveySchedules() {
        if (surveySchedules == null) {
            surveySchedules = new Select()
                    .from(SurveySchedule.class)
                    .where(Condition.column(SurveySchedule$Table.ID_SURVEY)
                            .eq(this.getId_survey())).queryList();
        }
        return surveySchedules;
    }

    /**
     * Returns the list of answered values from this survey that belong to a parent question
     */
    public List<Value> getValuesFromParentQuestions() {
        List<Value> values = new Select().all().from(Value.class).as("v")
                .join(Question.class, Join.JoinType.LEFT).as("q")
                .on(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_QUESTION))
                        .eq(ColumnAlias.columnWithTable("q", Question$Table.ID_QUESTION)))
                .where(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_SURVEY))
                        .eq(this.getId_survey()))
                .and(Condition.column(
                        ColumnAlias.columnWithTable("q", Question$Table.ID_PARENT)).isNull())
                .and(Condition.column(
                        ColumnAlias.columnWithTable("v", Value$Table.VALUE)).isNotNull())
                .and(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.VALUE)).isNot(
                        "")).queryList();
        //List<Value> values = Value.findWithQuery(Value.class, LIST_VALUES_PARENT_QUESTION, this
        // .getId().toString());
        return values;
    }

    /**
     * Ratio of completion is cached into answeredQuestionRatio in order to speed up loading
     */
    public SurveyAnsweredRatio getAnsweredQuestionRatio() {
        if (answeredQuestionRatio == null) {
            answeredQuestionRatio = SurveyAnsweredRatioCache.get(this.getId_survey());
            if (answeredQuestionRatio == null) {
                answeredQuestionRatio = reloadSurveyAnsweredRatio();
            }
        }
        return answeredQuestionRatio;
    }

    /**
     * Calculates the current ratio of completion for this survey
     *
     * @return SurveyAnsweredRatio that hold the total & answered questions.
     */
    private SurveyAnsweredRatio reloadSurveyAnsweredRatio() {

        SurveyAnsweredRatio surveyAnsweredRatio;
        //First parent is always required and not calculated.
        int numRequired = 1;
        int numAnswered = 0;

        Program program = Program.getFirstProgram();
        Tab tab = program.getTabs().get(0);
        Question rootQuestion = Question.findRootQuestion(tab);
        Question localQuestion = rootQuestion;
        while (localQuestion.getSibling() != null) {
            if (localQuestion.isCompulsory() && !localQuestion.isStockQuestion()) {
                numRequired++;
            }
            localQuestion = localQuestion.getSibling();
        }
        if (localQuestion.isStockQuestion() || !localQuestion.isCompulsory()) {
            numRequired--;
        }

        //Add children required by each parent (value+question)
        Survey survey = Survey.findById(id_survey);
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion().isCompulsory()) {
                numRequired += Question.countChildrenByOptionValue(value.getId_option());
            }
        }
        numAnswered += countCompulsoryBySurvey(this);
        Log.d("survey anserewed", "num required: " + numRequired + " num answered: " + numAnswered);
        surveyAnsweredRatio = new SurveyAnsweredRatio(numRequired, numAnswered);

        SurveyAnsweredRatioCache.put(this.id_survey, surveyAnsweredRatio);
        return surveyAnsweredRatio;
    }


    public static int countCompulsoryBySurvey(Survey survey) {
        List<Question> questions = survey.getQuestionsFromValues();
        int countOfCompulsoryValues = 0;
        for (Question question : questions) {
            if (question.isCompulsory()) {
                countOfCompulsoryValues++;
            }
        }
        return countOfCompulsoryValues;
    }

    /**
     * Return the number of optional questions like a counter by survey
     */
    private long countNumOptionalQuestionsAnswered() {
        long numOptionalQuestions = new Select().count().from(QuestionOption.class).as("qo")
                .join(Match.class, Join.JoinType.INNER).as("m")
                .on(
                        Condition.column(ColumnAlias.columnWithTable("m", Match$Table.ID_MATCH))
                                .eq(ColumnAlias.columnWithTable("qo",
                                        QuestionOption$Table.ID_MATCH)))

                .join(QuestionRelation.class, Join.JoinType.INNER).as("qr")
                .on(
                        Condition.column(ColumnAlias.columnWithTable("qr",
                                QuestionRelation$Table.ID_QUESTION_RELATION))
                                .eq(ColumnAlias.columnWithTable("m",
                                        Match$Table.ID_QUESTION_RELATION)))
                .join(Value.class, Join.JoinType.INNER).as("v")
                .on(
                        Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_QUESTION))
                                .eq(ColumnAlias.columnWithTable("qr",
                                        QuestionRelation$Table.ID_QUESTION)))
                .join(Question.class, Join.JoinType.INNER).as("q")
                .on(
                        Condition.column(
                                ColumnAlias.columnWithTable("q", Question$Table.ID_QUESTION))
                                .eq(ColumnAlias.columnWithTable("qr",
                                        QuestionRelation$Table.ID_QUESTION)))
                //Type of question-> Counter
                .where(Condition.column(ColumnAlias.columnWithTable("q", Question$Table.OUTPUT)).eq(
                        Constants.COUNTER))
                //For the given survey
                .and(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.ID_SURVEY)).eq(
                        this.getId_survey()))
                .count();

        //Parent with the right value -> not hidden
        return numOptionalQuestions;
    }

    /**
     * Updates ratios, status and completion date depending on the question and answer (text)
     */
    public void updateSurveyStatus() {
        //Sent surveys are not updated
        if (this.isSent() || this.isHide() || this.isConflict()) {
            return;
        }

        SurveyAnsweredRatio answeredRatio = this.reloadSurveyAnsweredRatio();

        //Update status & completionDate
        if (answeredRatio.isCompleted()) {
            this.setStatus(Constants.SURVEY_COMPLETED);
            this.setCompletionDate(new Date());
        } else {
            this.setStatus(Constants.SURVEY_IN_PROGRESS);
            this.setCompletionDate(this.eventDate);
        }
        //Saves new status & completionDate
        this.save();
    }

    /**
     * Checks if the answer to the first question is 'Yes'
     *
     * @return true|false
     */
    public boolean isRDT() {
        //refresh values
        getValuesFromDB();
        return getRDTName().equals(
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.rdtPositive));
    }

    /**
     * Since there are three possible values first question (RDT):'Positive','Negative','Not
     * Tested'
     *
     * @return String
     */
    public String getRDTName() {
        String rdtValue = "";
        if (values == null) {
            values = Value.listAllBySurvey(this);
        }

        if (values.size() > 0) {
            for (Value value : values) {
                //Find the RTS option
                if (value.getOption() != null && value.getQuestion() != null
                        && value.getQuestion().getCode().equals(
                        PreferencesState.getInstance().getContext().getString(R.string.RDT_code))) {
                    rdtValue = value.getOption().getName();
                }
            }

        }
        return rdtValue;
    }

    /**
     * Since there are three possible values first question (RDT):'Positive','Negative','Not
     * Tested'
     *
     * @return String
     */
    public String getResultCode() {
        String rdtValue = "";
        if (values == null) {
            values = Value.listAllBySurvey(this);
        }

        if (values.size() > 0) {
            for (Value value : values) {
                //Find the RTS option
                if (value.getOption() != null && value.getQuestion() != null
                        && value.getQuestion().getCode().equals(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.Result_code))) {
                    rdtValue = value.getOption().getInternationalizedCode();
                }
            }

        }
        return rdtValue;
    }

    /**
     * Moves the schedule date for this survey to a new given date due to a given reason (comment)
     */
    public void reschedule(Date newScheduledDate, String comment) {
        //Take currentDate
        Date currentScheduleDate = this.getScheduledDate();

        //Add a history
        SurveySchedule previousSchedule = new SurveySchedule(this, currentScheduleDate, comment);
        previousSchedule.save();

        //Clean inner lazy schedulelist
        surveySchedules = null;

        //Move scheduledate and save
        this.scheduledDate = newScheduledDate;
        this.save();
    }

    public void prepareSurveyCompletionDate() {
        if (!isSent()) {
            setCompletionDate(new Date());
            save();
        }
    }

    /**
     * Turns all values from a survey into a string with values separated by commas
     *
     * @return String
     */
    public String getValuesToString() {
        if (values == null) {
            values = Value.listAllBySurvey(this);
        }

        Iterator<Value> iterator = values.iterator();

        String valuesStr = "";

        //Define a filter to select which values will be turned into string by code_question
        List<Question> questions = Question.getAllQuestions();
        List<String> codeQuestionFilter = new ArrayList<String>();

        for (Question question : questions) {
            if (question.isVisible()) {
                codeQuestionFilter.add(question.getCode());
            }
        }

        Map map = new HashMap();
        while (iterator.hasNext()) {
            Value value = iterator.next();
            //The control dataelements not have questions and its should be ignored
            if (value.getQuestion() == null || value.getValue() == null) {
                continue;
            }
            String qCode = value.getQuestion().getCode();

            if (codeQuestionFilter.contains(qCode)) {
                String val =
                        (value.getOption() != null) ? value.getOption().getInternationalizedCode()
                                : value.getValue();
                if (val != null) {
                    map.put(qCode, val);
                }
            }
        }
        //Sort values
        for (int i = 0; i < codeQuestionFilter.size(); i++) {
            if (map.get(codeQuestionFilter.get(i)) != null) {
                valuesStr += map.get(codeQuestionFilter.get(i));
                if (i < codeQuestionFilter.size() - 1) {
                    valuesStr += ", ";
                }
            }
        }
        if (valuesStr.endsWith(", ")) {
            valuesStr = valuesStr.substring(0, valuesStr.lastIndexOf(", "));
        }
        return valuesStr;
    }

    public String printValues() {
        String valuesString = "Survey values: ";
        if (getValuesFromDB() != null) {
            for (Value value : values) {
                valuesString += "Value: " + value.getValue();
                if (value.getOption() != null) {
                    valuesString += " Option: " + value.getOption().getInternationalizedName();
                }
                if (value.getQuestion() != null) {
                    valuesString += " Question: " + value.getQuestion().getDe_name() + "\n";
                }
            }
        }
        return valuesString;
    }

    /**
     * This method removes the children question values from when a parent question is removed
     *
     * @removeCounters is used to prevent to remove the counter when change the some question in the
     * same level
     * This changed was required because the counter need be child of the question+option who tigger
     * the counter.
     */
    public void removeChildrenValuesFromQuestionRecursively(Question question,
            boolean removeCounters) {
        List<Value> values = getValuesFromDB();
        List<Question> questionChildren = question.getChildren();
        for (int i = values.size() - 1; i > 0; i--) {
            //This loop removes recursively the values on the children question
            if (questionChildren.contains(values.get(i).getQuestion())) {
                //Remove the children values but if the child value is a counter is not removed in the first level
                if (!values.get(i).getQuestion().isACounter() || removeCounters) {
                    removeValue(values.get(i));
                }
                for (Question child : questionChildren) {
                    removeChildrenValuesFromQuestionRecursively(child, true);
                }
            }
        }
    }

    /**
     * This method get the return the highest number of total pages in the survey question values.
     */
    public int getMaxTotalPages() {
        List<Value> values = getValuesFromDB();
        int totalPages = 0;
        for (Value value : values) {
            if (value.getQuestion() != null
                    && value.getQuestion().getTotalQuestions() > totalPages) {
                totalPages = value.getQuestion().getTotalQuestions();
            }
        }
        return totalPages;
    }

    @Override
    public void accept(IConvertToSDKVisitor IConvertToSDKVisitor) throws Exception {
        IConvertToSDKVisitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (id_survey != survey.id_survey) return false;
        if (id_program != null ? !id_program.equals(survey.id_program)
                : survey.id_program != null) {
            return false;
        }
        if (id_org_unit != null ? !id_org_unit.equals(survey.id_org_unit)
                : survey.id_org_unit != null) {
            return false;
        }
        if (id_user != null ? !id_user.equals(survey.id_user) : survey.id_user != null) {
            return false;
        }
        if (creationDate != null ? !creationDate.equals(survey.creationDate)
                : survey.creationDate != null) {
            return false;
        }
        if (completionDate != null ? !completionDate.equals(survey.completionDate)
                : survey.completionDate != null) {
            return false;
        }
        if (eventDate != null ? !eventDate.equals(survey.eventDate) : survey.eventDate != null) {
            return false;
        }
        if (scheduledDate != null ? !scheduledDate.equals(survey.scheduledDate)
                : survey.scheduledDate != null) {
            return false;
        }
        return !(status != null ? !status.equals(survey.status) : survey.status != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_survey ^ (id_survey >>> 32));
        result = 31 * result + (id_program != null ? id_program.hashCode() : 0);
        result = 31 * result + (id_org_unit != null ? id_org_unit.hashCode() : 0);
        result = 31 * result + (id_user != null ? id_user.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (completionDate != null ? completionDate.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        result = 31 * result + (scheduledDate != null ? scheduledDate.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id_survey=" + id_survey +
                ", id_program=" + id_program +
                ", id_org_unit=" + id_org_unit +
                ", id_user=" + id_user +
                ", creationDate=" + creationDate +
                ", completionDate=" + completionDate +
                ", eventDate=" + eventDate +
                ", scheduledDate=" + scheduledDate +
                ", status=" + status +
                '}';
    }


    public Float getCounterValue(Question question, Option selectedOption) {
        Question optionCounter = question.findCounterByOption(selectedOption);

        if (optionCounter == null) {
            return 0f;
        }

        String counterValue = ReadWriteDB.readValueQuestion(optionCounter);
        if (counterValue == null || counterValue.isEmpty()) {
            return 0f;
        }

        return Float.parseFloat(counterValue);
    }

    public HashMap<Tab, Integer> getAnsweredTabs() {
        HashMap<Tab, Integer> tabs = new HashMap<Tab, Integer>();

        List<Value> values = getValuesFromDB();
        int tabSize = 0;
        for (Value value : values) {
            Tab tab = value.getQuestion().getHeader().getTab();
            if (!tabs.containsKey(tab)) {
                tabs.put(tab, tabSize);
                tabSize++;
            }
        }
        return tabs;
    }
}
