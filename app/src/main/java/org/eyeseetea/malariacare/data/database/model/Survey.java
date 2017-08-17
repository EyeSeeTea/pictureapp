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

import static org.eyeseetea.malariacare.data.database.AppDatabase.matchAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.matchName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.orgUnitAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.orgUnitName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.programName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionOptionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionRelationAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionRelationName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.surveyName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.valueName;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.ProcessModelTransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.Transaction;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.database.utils.SurveyAnsweredRatioCache;
import org.eyeseetea.malariacare.data.sync.exporter.IConvertToSDKVisitor;
import org.eyeseetea.malariacare.data.sync.exporter.VisitableToSDK;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.strategies.SurveyFragmentStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Table(database = AppDatabase.class)
public class Survey extends BaseModel implements VisitableToSDK {
    public static final float MAX_AMBER = 80f;
    public static final float MAX_RED = 50f;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_survey;

    @Column
    Long id_program_fk;
    /**
     * Reference to the program associated to this survey (loaded lazily)
     */
    Program program;

    @Column
    Long id_org_unit_fk;
    /**
     * Reference to the org unit associated to this survey (loaded lazily)
     */
    OrgUnit orgUnit;

    @Column
    Long id_user_fk;
    /**
     * Reference to the user that has created this survey (loaded lazily)
     */
    User user;

    @Column
    Date creation_date;

    @Column
    Date completion_date;

    @Column
    Date event_date;

    @Column
    Date scheduled_date;

    @Column
    Integer status;

    @Column
    Integer type;

    @Column
    String uid_event_fk;

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
        this.creation_date = new Date();
        this.completion_date = this.creation_date;
        this.event_date = new Date();
        this.scheduled_date = null;
        this.type = Constants.SURVEY_NO_TYPE; //to avoid NullPointerExceptions
    }

    public Survey(OrgUnit orgUnit, Program program, User user) {
        this();

        // Possibilities [ In progress | Completed | Sent ]
        this.status = Constants.SURVEY_IN_PROGRESS;

        //Set context of the survey
        this.setOrgUnit(orgUnit);
        this.setProgram(program);
        this.setUser(user);
        this.setType(Constants.SURVEY_NO_TYPE);
    }

    public Survey(OrgUnit orgUnit, Program program, User user, int type) {
        this();

        // Possibilities [ In progress | Completed | Sent ]
        this.status = Constants.SURVEY_IN_PROGRESS;

        //Set context of the survey
        this.setOrgUnit(orgUnit);
        this.setProgram(program);
        this.setUser(user);
        this.type = type;
    }


    public Long getId_survey() {
        return id_survey;
    }

    public void setId_survey(Long id_survey) {
        this.id_survey = id_survey;
    }

    public OrgUnit getOrgUnit() {
        if (orgUnit == null) {
            if (id_org_unit_fk == null) return null;
            orgUnit = new Select()
                    .from(OrgUnit.class)
                    .where(OrgUnit_Table.id_org_unit
                            .is(id_org_unit_fk)).querySingle();
        }
        return orgUnit;
    }

    public void setOrgUnit(Long id_org_unit) {
        this.id_org_unit_fk = id_org_unit;
        this.orgUnit = null;
    }

    public void setOrgUnit(OrgUnit orgUnit) {
        this.orgUnit = orgUnit;
        this.id_org_unit_fk = (orgUnit != null) ? orgUnit.getId_org_unit() : null;
    }

    public Program getProgram() {
        if (program == null) {
            if (id_program_fk == null) return null;
            program = new Select()
                    .from(Program.class)
                    .where(Program_Table.id_program
                            .is(id_program_fk)).querySingle();
        }
        return program;
    }

    public void setProgram(Long id_program) {
        this.id_program_fk = id_program;
        this.program = null;
    }

    public void setProgram(Program program) {
        this.program = program;
        this.id_program_fk = (program != null) ? program.getId_program() : null;
    }

    public User getUser() {
        if (user == null) {
            if (id_user_fk == null) return null;
            user = new Select()
                    .from(User.class)
                    .where(User_Table.id_user
                            .is(id_user_fk)).querySingle();
        }
        return user;
    }

    public void setUser(Long id_user) {
        this.id_user_fk = id_user;
        this.user = null;
    }

    public void setUser(User user) {
        this.user = user;
        this.id_user_fk = (user != null) ? user.getId_user() : null;
    }
    public Date getCreationDate() {
        return creation_date;
    }

    public void setCreationDate(Date creationDate) {
        this.creation_date = creationDate;
    }

    public Date getCompletionDate() {
        return completion_date;
    }

    public void setCompletionDate(Date completionDate) {
        this.completion_date = completionDate;
    }

    public Date getEventDate() {
        return event_date;
    }

    public void setEventDate(Date eventDate) {
        this.event_date = eventDate;
    }

    public Date getScheduledDate() {
        return scheduled_date;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduled_date = scheduledDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getEventUid() {
        return uid_event_fk;
    }

    public void setEventUid(EventFlow event) {
        this.uid_event_fk = event.getUId();
    }

    public void setEventUid(String eventuid) {
        this.uid_event_fk = eventuid;
    }

    /**
     * Returns a concrete survey, if it exists
     */
    public static List<Survey> getUnsentSurveys(OrgUnit orgUnit, Program program) {
        return new Select().from(Survey.class)
                .where(Survey_Table.id_org_unit_fk.eq(orgUnit.getId_org_unit()))
                .and(Survey_Table.id_program_fk.eq(program.getId_program()))
                .and(Survey_Table.status.isNot(Constants.SURVEY_SENT))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the malaria surveys with status yet not put to "Sent"
     */
    public static List<Survey> getAllUnsentMalariaSurveys(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Survey_Table.status.withTable(surveyAlias)
                        .isNot(Constants.SURVEY_SENT))
                .and(Survey_Table.status.withTable(surveyAlias)
                        .isNot(Constants.SURVEY_CONFLICT))
                .and(Program_Table.uid_program.withTable(programAlias)
                        .is(malariaProgramUid))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date.withTable(surveyAlias)))
                .orderBy(OrderBy.fromProperty(
                        Survey_Table.id_org_unit_fk.withTable(surveyAlias))).queryList();
    }

    /**
     * Returns all the malaria surveys with status put to "Sent"
     */
    public static List<Survey> getAllSentMalariaSurveys(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))

                .where(ConditionGroup.clause()
                        .and(Program_Table.uid_program.withTable(programAlias)
                                .is(malariaProgramUid))
                        .and(ConditionGroup.clause()
                                .and(Survey_Table.status.withTable(surveyAlias)
                                        .is(Constants.SURVEY_SENT)))
                .or(Survey_Table.status.eq(Constants.SURVEY_CONFLICT)))
                .orderBy(Survey_Table.event_date, false).queryList();
    }

    /**
     * Returns all the surveys with status yet not put to "Sent"
     */
    public static List<Survey> getAllUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.isNot(Constants.SURVEY_SENT))
                .and(Survey_Table.status.isNot(Constants.SURVEY_CONFLICT))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status yet not put to "Sent"
     */
    public static List<Survey> getUnsentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.isNot(Constants.SURVEY_SENT))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<Survey> getAllSentSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_SENT))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending()).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static List<Survey> getAllQuarantineSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending()).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static int countQuarantineSurveys() {
        return (int) SQLite.selectCountOf()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .count();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<Survey> getAllMalariaSurveysToBeSent(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Survey_Table.status.withTable(surveyAlias)
                        .eq(Constants.SURVEY_COMPLETED))
                .and(Program_Table.uid_program.withTable(programAlias)
                        .is(malariaProgramUid))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<Survey> getAllSurveysToBeSent() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys
     */
    public static List<Survey> getAllSurveys() {
        return new Select().from(Survey.class)
                .orderBy(OrderBy.fromProperty(Survey_Table.completion_date)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Sent"
     */
    public static List<Survey> getSentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_SENT))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Completed"
     */
    public static List<Survey> getAllCompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    public static List<Survey> getAllCompletedSurveysNoReceiptReset() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_COMPLETED))
                .and(Survey_Table.type.isNot(Constants.SURVEY_RECEIPT))
                .and(Survey_Table.type.isNot(Constants.SURVEY_RESET))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Completed"
     */
    public static List<Survey> getCompletedSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_COMPLETED))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "In progress"
     */
    public static List<Survey> getAllUncompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.is(Constants.SURVEY_IN_PROGRESS))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "In progress"
     */
    public static List<Survey> getUncompletedSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_IN_PROGRESS))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<Survey> getAllUncompletedUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.isNot(Constants.SURVEY_COMPLETED))
                .or(Survey_Table.status.isNot(Constants.SURVEY_SENT))
                .or(Survey_Table.status.isNot(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<Survey> getAllCompletedUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.is(Constants.SURVEY_COMPLETED))
                .or(Survey_Table.status.isNot(Constants.SURVEY_SENT))
                .or(Survey_Table.status.isNot(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent" or completed
     */
    public static List<Survey> getAllSentOrCompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_SENT))
                .or(Survey_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
    }

    // Returns all the surveys with status put to "Hide"
    public static List<Survey> getAllHideAndSentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_SENT))
                .limit(limit)
                .or(Survey_Table.status.eq(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date))
                .orderBy(OrderBy.fromProperty(Survey_Table.id_org_unit_fk)).queryList();
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
                .where(Survey_Table.status.eq(Constants.SURVEY_SENT))
                .or(Survey_Table.status.eq(Constants.SURVEY_CONFLICT))
                .and(Survey_Table.event_date.greaterThanOrEq(
                        minDateForMonitor)).queryList();
    }

    /**
     * Find the surveys for a program, with a type o no type ans with the event date grater than
     * passed.
     *
     * @param program The program of the survey
     * @param date    The min eventDate of the survey
     * @return A list of surveys
     */
    public static List<Survey> findSurveysWithProgramAndGreaterDate(Program program, Date date) {
        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Program_Table.id_program.withTable(programAlias)
                        .eq(program.getId_program()))
                .and(Survey_Table.event_date.withTable(surveyAlias).greaterThanOrEq(
                        date)).queryList();
    }

    public static Date getLastDateForSurveyType(int type) {
        Survey survey = new Select().
                from(Survey.class)
                .where(Survey_Table.type.eq(type))
                .groupBy(Survey_Table.id_survey)
                .having(Survey_Table.event_date.is(Method.max(Survey_Table.event_date)))
                .querySingle();
        if (survey == null) {
            return new Date(0);
        }
        return survey.getEventDate();
    }

    public static Survey getLastSurveyWithType(int type) {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Survey_Table.type.eq(type))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending())
                .querySingle();
        return survey;
    }


    public static List<Survey> getSurveysWithProgramType(Program program, int type) {
        return new Select().from(Survey.class).as(surveyName)
                .join(Program.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(Survey_Table.id_program_fk.withTable(surveyAlias)
                        .eq(Program_Table.id_program.withTable(programAlias)))
                .where(Program_Table.id_program.withTable(programAlias)
                        .eq(program.getId_program()))
                .and(Survey_Table.type.withTable(surveyAlias).is(type)).queryList();
    }

    /**
     * Finds a survey by its ID
     */
    public static Survey findById(Long id_survey) {
        return new Select()
                .from(Survey.class)
                .where(Survey_Table.id_survey
                        .eq(id_survey))
                .querySingle();
    }

    public static void removeValue(Value value) {
        value.delete();
    }

    public static int countSurveysByCompletiondate(Date completion_date) {

        return (int) SQLite.selectCountOf()
                .from(Survey.class)
                .where(Survey_Table.completion_date.eq(completion_date))
                .count();
    }

    public static Date getMinQuarantineEventDate() {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).ascending())
                .querySingle();
        return survey.getEventDate();
    }

    public static Date getMaxQuarantineEventDate() {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending())
                .querySingle();
        return survey.getEventDate();
    }

    public static List<Survey> getAllSendingSurveys() {
        return new Select()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_SENDING))
                .queryList();
    }

    public Float getCounterValue(Question question, Option selectedOption) {
        Question optionCounter = question.findCounterByOption(selectedOption);
        if (optionCounter == null) {
            return 0f;
        }
        String counterValue = optionCounter.getQuestionValueBySession();
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
                .where(Survey_Table.id_survey.eq(idSurvey)).querySingle();

        return srv.getStatus().equals(Constants.SURVEY_COMPLETED);
    }

    /**
     * Checks if the survey is in progress
     *
     * @return true|false
     */
    public boolean isInProgress() {
        return this.status == Constants.SURVEY_IN_PROGRESS;
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
                .where(Score_Table.id_survey_fk.eq(
                        this.getId_survey())).querySingle();
    }

    @Override
    public void delete() {
        Score score = getScore();
        if (score != null) {
            score.delete();
        }
        for (Value value : getValuesFromDB()) {
            value.delete();
        }
        super.delete();
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
                    .where(Value_Table.id_survey_fk
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
                .where(Value_Table.id_survey_fk
                        .eq(this.getId_survey())).queryList();
        return values;
    }


    /**
     * Returns the list of answered values from this survey
     */
    public List<Value> getValuesFromDBWithoutSave() {
        return new Select()
                .from(Value.class)
                .where(Value_Table.id_survey_fk
                        .eq(this.getId_survey())).queryList();
    }
    /**
     * Returns the list of questions from answered values
     */
    public List<Question> getQuestionsFromValues() {
        List<Question> questions = new Select()
                .from(Question.class).as(questionName)
                .join(Value.class, Join.JoinType.LEFT_OUTER).as(valueName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                        .eq(Question_Table.id_question.withTable(questionAlias)))
                .where(Value_Table.id_survey_fk.withTable(valueAlias)
                        .eq(this.getId_survey()))
                .orderBy(OrderBy.fromProperty(Question_Table.order_pos).ascending()).queryList();
        return questions;
    }

    /**
     * Returns the list of previous schedules for this survey
     */
    public List<SurveySchedule> getSurveySchedules() {
        if (surveySchedules == null) {
            surveySchedules = new Select()
                    .from(SurveySchedule.class)
                    .where(SurveySchedule_Table.id_survey_fk
                            .eq(this.getId_survey())).queryList();
        }
        return surveySchedules;
    }

    /**
     * Returns the list of answered values from this survey that belong to a parent question
     */
    public List<Value> getValuesFromParentQuestions() {
        List<Value> values = new Select().from(Value.class).as(valueName)
                .join(Question.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                        .eq(Question_Table.id_question.withTable(questionAlias)))
                .where(Value_Table.id_survey_fk.withTable(valueAlias)
                        .eq(this.getId_survey()))
                .and(Question_Table.id_question_parent.withTable(questionAlias).isNull())
                .and(Value_Table.value.withTable(valueAlias).isNotNull())
                .and(Value_Table.value.withTable(valueAlias).isNot(
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
    public SurveyAnsweredRatio reloadSurveyAnsweredRatio() {
        SurveyAnsweredRatio surveyAnsweredRatio;
        //First parent is always required and not calculated.
        int numRequired = 1;
        int numAnswered = 0;

        IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
        Program program = Program.findByUID(programLocalDataSource.getUserProgram().getId());
        Tab tab = program.getTabs().get(0);
        Question rootQuestion = Question.findRootQuestion(tab);
        Question localQuestion = rootQuestion;
        numRequired = SurveyFragmentStrategy.getNumRequired(numRequired, localQuestion);

        //Add children required by each parent (value+question)
        Survey survey = Survey.findById(id_survey);
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion().isCompulsory() && value.getId_option() != null) {
                numRequired += Question.countChildrenByOptionValue(value.getId_option());
            }
        }
        numAnswered += countCompulsoryBySurvey(this);
        Log.d("survey answered", "num required: " + numRequired + " num answered: " + numAnswered);
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
        long numOptionalQuestions = SQLite.selectCountOf().from(QuestionOption.class).as(
                questionOptionName)
                .join(Match.class, Join.JoinType.INNER).as(matchName)
                .on(Match_Table.id_match.withTable(matchAlias)
                        .eq(QuestionOption_Table.id_match_fk.withTable(questionOptionAlias)))

                .join(QuestionRelation.class, Join.JoinType.INNER).as(questionRelationName)
                .on(QuestionRelation_Table.id_question_relation.withTable(questionRelationAlias)
                        .eq(Match_Table.id_question_relation_fk.withTable(matchAlias)))
                .join(Value.class, Join.JoinType.INNER).as(valueName)
                .on(Value_Table.id_question_fk.withTable(valueAlias)
                        .eq(QuestionRelation_Table.id_question_fk))
                .join(Question.class, Join.JoinType.INNER).as(questionName)
                .on(Question_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelation_Table.id_question_fk.withTable(questionRelationAlias)))
                //Type of question-> Counter
                .where(Question_Table.output.eq(
                        Constants.COUNTER))
                //For the given survey
                .and(Value_Table.id_survey_fk.eq(
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
            complete();
        } else {
            setStatus(Constants.SURVEY_IN_PROGRESS);
            setCompletionDate(this.event_date);
            save();
        }
    }

    public void complete() {
        setStatus(Constants.SURVEY_COMPLETED);
        setCompletionDate(new Date());
        save();
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

    public boolean isIssueSurvey() {
        if (type == null) {
            return false;
        }
        return type == Constants.SURVEY_ISSUE;
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
                    rdtValue = value.getOption().getCode();
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
                    rdtValue = value.getOption().getInternationalizedName();
                }
            }

        }
        return rdtValue;
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
        List<Question> questions = new ArrayList<>();
        for (Value value : values) {
            questions.add(value.getQuestion());
        }
        List<String> codeQuestionFilter = new ArrayList<String>();

        for (Question question : questions) {
            if (question != null && question.isVisible()) {
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
                        (value.getOption() != null) ? value.getOption().getInternationalizedName()
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
                    valuesString += " Option: " + value.getOption().getInternationalizedCode();
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
     * @removeCounters is used to prevent to remove the counter when change the some question in
     * the
     * same level
     * This changed was required because the counter need be child of the question+option who
     * tigger
     * the counter.
     */
    public void removeChildrenValuesFromQuestionRecursively(Question question,
            boolean removeCounters) {
        List<Value> values = getValuesFromDB();
        List<Question> questionChildren = question.getChildren();
        for (int i = values.size() - 1; i > 0; i--) {
            //This loop removes recursively the values on the children question
            if (questionChildren.contains(values.get(i).getQuestion())) {
                //Remove the children values but if the child value is a counter is not removed
                // in the first level
                if (!values.get(i).getQuestion().isACounter() || removeCounters) {
                    for(Question questionPropagated:values.get(i).getQuestion().getPropagationQuestions()){
                        removeValue(questionPropagated.getValueBySurvey(Session.getMalariaSurvey()));
                    }
                    removeValue(values.get(i));
                }
                for (Question child : questionChildren) {
                    removeChildrenValuesFromQuestionRecursively(child, true);
                }
            }
        }
    }


    public static List<OrgUnit> getQuarantineOrgUnits(long programUid) {
        return new Select().from(OrgUnit.class) .as(orgUnitName)
        .join(Survey.class, Join.JoinType.LEFT_OUTER).as(surveyName)
                .on(Survey_Table.id_org_unit_fk.withTable(surveyAlias)
                        .eq(OrgUnit_Table.id_org_unit.withTable(orgUnitAlias)))
            .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(Survey_Table.id_program_fk.eq(programUid)).queryList();
    }

    public static List<Survey> getAllQuarantineSurveysByProgramAndOrgUnit(Program program, OrgUnit orgUnit) {
        return new Select().from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(Survey_Table.id_program_fk.eq(program.getId_program()))
                .and(Survey_Table.id_org_unit_fk.eq(orgUnit.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending()).queryList();
    }

    public static Date getMinQuarantineCompletionDateByProgramAndOrgUnit(Program program,
            OrgUnit orgUnit) {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(Survey_Table.id_program_fk.eq(program.getId_program()))
                .and(Survey_Table.id_org_unit_fk.eq(orgUnit.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(Survey_Table.creation_date).ascending())
                .querySingle();
        return survey.getCreationDate();
    }

    public static Date getMaxQuarantineEventDateByProgramAndOrgUnit(Program program,
            OrgUnit orgUnit) {
        Survey survey = new Select()
                .from(Survey.class)
                .where(Survey_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(Survey_Table.id_program_fk.eq(program.getId_program()))
                .and(Survey_Table.id_org_unit_fk.eq(orgUnit.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(Survey_Table.event_date).descending())
                .querySingle();
        return survey.getEventDate();
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
    public void accept(IConvertToSDKVisitor IConvertToSDKVisitor) throws ConversionException {
        IConvertToSDKVisitor.visit(this);
    }

    public static void saveAll(List<Survey> surveys, final IDataSourceCallback<Void> callback) {

        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<Survey>() {
                            @Override
                            public void processModel(Survey survey) {
                                survey.save();
                            }
                        }).addAll(surveys).build())
                .error(new Transaction.Error() {
                    @Override
                    public void onError(Transaction transaction, Throwable error) {
                        callback.onError(error);
                    }
                })
                .success(new Transaction.Success() {
                    @Override
                    public void onSuccess(Transaction transaction) {
                        callback.onSuccess(null);
                    }
                }).build().execute();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (id_survey != survey.id_survey) return false;
        if (id_program_fk != null ? !id_program_fk.equals(survey.id_program_fk)
                : survey.id_program_fk != null) {
            return false;
        }
        if (id_org_unit_fk != null ? !id_org_unit_fk.equals(survey.id_org_unit_fk)
                : survey.id_org_unit_fk != null) {
            return false;
        }
        if (id_user_fk != null ? !id_user_fk.equals(survey.id_user_fk) : survey.id_user_fk != null) {
            return false;
        }
        if (creation_date != null ? !creation_date.equals(survey.creation_date)
                : survey.creation_date != null) {
            return false;
        }
        if (completion_date != null ? !completion_date.equals(survey.completion_date)
                : survey.completion_date != null) {
            return false;
        }
        if (event_date != null ? !event_date.equals(survey.event_date) : survey.event_date != null) {
            return false;
        }
        if (scheduled_date != null ? !scheduled_date.equals(survey.scheduled_date)
                : survey.scheduled_date != null) {
            return false;
        }
        if (status != null ? !status.equals(survey.status) : survey.status != null) return false;
        return type != null ? type.equals(survey.type) : survey.type == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_survey ^ (id_survey >>> 32));
        result = 31 * result + (id_program_fk != null ? id_program_fk.hashCode() : 0);
        result = 31 * result + (id_org_unit_fk != null ? id_org_unit_fk.hashCode() : 0);
        result = 31 * result + (id_user_fk != null ? id_user_fk.hashCode() : 0);
        result = 31 * result + (creation_date != null ? creation_date.hashCode() : 0);
        result = 31 * result + (completion_date != null ? completion_date.hashCode() : 0);
        result = 31 * result + (event_date != null ? event_date.hashCode() : 0);
        result = 31 * result + (scheduled_date != null ? scheduled_date.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id_survey=" + id_survey +
                ", id_program_fk=" + id_program_fk +
                ", id_org_unit_fk=" + id_org_unit_fk +
                ", id_user_fk=" + id_user_fk +
                ", creation_date=" + creation_date +
                ", completion_date=" + completion_date +
                ", event_date=" + event_date +
                ", scheduled_date=" + scheduled_date +
                ", status=" + status +
                ", type=" + type +
                '}';
    }

    public static boolean findByProgramUid(String string) {
        return false;
    }
}
