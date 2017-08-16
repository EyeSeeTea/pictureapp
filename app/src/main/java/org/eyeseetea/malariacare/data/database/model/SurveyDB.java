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

@Table(database = AppDatabase.class, name = "Survey")
public class SurveyDB extends BaseModel implements VisitableToSDK {
    public static final float MAX_AMBER = 80f;
    public static final float MAX_RED = 50f;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_survey;

    @Column
    Long id_program_fk;
    /**
     * Reference to the mProgramDB associated to this survey (loaded lazily)
     */
    ProgramDB mProgramDB;

    @Column
    Long id_org_unit_fk;
    /**
     * Reference to the org unit associated to this survey (loaded lazily)
     */
    OrgUnitDB mOrgUnitDB;

    @Column
    Long id_user_fk;
    /**
     * Reference to the mUserDB that has created this survey (loaded lazily)
     */
    UserDB mUserDB;

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
    List<ValueDB> mValueDBs;

    /**
     * List of historic previous schedules
     */
    List<SurveyScheduleDB> mSurveyScheduleDBs;

    /**
     * Calculated answered ratio for this survey according to its values
     */
    SurveyAnsweredRatio answeredQuestionRatio;

    /**
     * Calculated main ScoreDB for this survey, is not persisted, just calculated on runtime
     */
    Float mainScore;

    public SurveyDB() {
        //Set dates
        this.creation_date = new Date();
        this.completion_date = this.creation_date;
        this.event_date = new Date();
        this.scheduled_date = null;
        this.type = Constants.SURVEY_NO_TYPE; //to avoid NullPointerExceptions
    }

    public SurveyDB(OrgUnitDB orgUnitDB, ProgramDB programDB, UserDB userDB) {
        this();

        // Possibilities [ In progress | Completed | Sent ]
        this.status = Constants.SURVEY_IN_PROGRESS;

        //Set context of the survey
        this.setOrgUnit(orgUnitDB);
        this.setProgram(programDB);
        this.setUser(userDB);
        this.setType(Constants.SURVEY_NO_TYPE);
    }

    public SurveyDB(OrgUnitDB orgUnitDB, ProgramDB programDB, UserDB userDB, int type) {
        this();

        // Possibilities [ In progress | Completed | Sent ]
        this.status = Constants.SURVEY_IN_PROGRESS;

        //Set context of the survey
        this.setOrgUnit(orgUnitDB);
        this.setProgram(programDB);
        this.setUser(userDB);
        this.type = type;
    }


    public Long getId_survey() {
        return id_survey;
    }

    public void setId_survey(Long id_survey) {
        this.id_survey = id_survey;
    }

    public OrgUnitDB getOrgUnitDB() {
        if (mOrgUnitDB == null) {
            if (id_org_unit_fk == null) return null;
            mOrgUnitDB = new Select()
                    .from(OrgUnitDB.class)
                    .where(OrgUnitDB_Table.id_org_unit
                            .is(id_org_unit_fk)).querySingle();
        }
        return mOrgUnitDB;
    }

    public void setOrgUnitDB(Long id_org_unit) {
        this.id_org_unit_fk = id_org_unit;
        this.mOrgUnitDB = null;
    }

    public void setOrgUnit(OrgUnitDB orgUnitDB) {
        this.mOrgUnitDB = orgUnitDB;
        this.id_org_unit_fk = (orgUnitDB != null) ? orgUnitDB.getId_org_unit() : null;
    }

    public ProgramDB getProgramDB() {
        if (mProgramDB == null) {
            if (id_program_fk == null) return null;
            mProgramDB = new Select()
                    .from(ProgramDB.class)
                    .where(ProgramDB_Table.id_program
                            .is(id_program_fk)).querySingle();
        }
        return mProgramDB;
    }

    public void setProgramDB(Long id_program) {
        this.id_program_fk = id_program;
        this.mProgramDB = null;
    }

    public void setProgram(ProgramDB programDB) {
        this.mProgramDB = programDB;
        this.id_program_fk = (programDB != null) ? programDB.getId_program() : null;
    }

    public UserDB getUserDB() {
        if (mUserDB == null) {
            if (id_user_fk == null) return null;
            mUserDB = new Select()
                    .from(UserDB.class)
                    .where(UserDB_Table.id_user
                            .is(id_user_fk)).querySingle();
        }
        return mUserDB;
    }

    public void setUserDB(Long id_user) {
        this.id_user_fk = id_user;
        this.mUserDB = null;
    }

    public void setUser(UserDB userDB) {
        this.mUserDB = userDB;
        this.id_user_fk = (userDB != null) ? userDB.getId_user() : null;
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
    public static List<SurveyDB> getUnsentSurveys(OrgUnitDB orgUnitDB, ProgramDB programDB) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.id_org_unit_fk.eq(orgUnitDB.getId_org_unit()))
                .and(SurveyDB_Table.id_program_fk.eq(programDB.getId_program()))
                .and(SurveyDB_Table.status.isNot(Constants.SURVEY_SENT))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the malaria surveys with status yet not put to "Sent"
     */
    public static List<SurveyDB> getAllUnsentMalariaSurveys(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(SurveyDB.class).as(surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .where(SurveyDB_Table.status.withTable(surveyAlias)
                        .isNot(Constants.SURVEY_SENT))
                .and(SurveyDB_Table.status.withTable(surveyAlias)
                        .isNot(Constants.SURVEY_CONFLICT))
                .and(ProgramDB_Table.uid_program.withTable(programAlias)
                        .is(malariaProgramUid))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date.withTable(surveyAlias)))
                .orderBy(OrderBy.fromProperty(
                        SurveyDB_Table.id_org_unit_fk.withTable(surveyAlias))).queryList();
    }

    /**
     * Returns all the malaria surveys with status put to "Sent"
     */
    public static List<SurveyDB> getAllSentMalariaSurveys(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(SurveyDB.class).as(surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))

                .where(ConditionGroup.clause()
                        .and(ProgramDB_Table.uid_program.withTable(programAlias)
                                .is(malariaProgramUid))
                        .and(ConditionGroup.clause()
                                .and(SurveyDB_Table.status.withTable(surveyAlias)
                                        .is(Constants.SURVEY_SENT)))
                        .or(SurveyDB_Table.status.eq(Constants.SURVEY_CONFLICT)))
                .orderBy(SurveyDB_Table.event_date, false).queryList();
    }

    /**
     * Returns all the surveys with status yet not put to "Sent"
     */
    public static List<SurveyDB> getAllUnsentSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.isNot(Constants.SURVEY_SENT))
                .and(SurveyDB_Table.status.isNot(Constants.SURVEY_CONFLICT))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status yet not put to "Sent"
     */
    public static List<SurveyDB> getUnsentSurveys(int limit) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.isNot(Constants.SURVEY_SENT))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<SurveyDB> getAllSentSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENT))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending()).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static List<SurveyDB> getAllQuarantineSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending()).queryList();
    }

    /**
     * Returns all the surveys with status put to "quarantine"
     */
    public static int countQuarantineSurveys() {
        return (int) SQLite.selectCountOf()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .count();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<SurveyDB> getAllMalariaSurveysToBeSent(String malariaProgramUid) {
        Context context = PreferencesState.getInstance().getContext();
        return new Select().from(SurveyDB.class).as(surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .where(SurveyDB_Table.status.withTable(surveyAlias)
                        .eq(Constants.SURVEY_COMPLETED))
                .and(ProgramDB_Table.uid_program.withTable(programAlias)
                        .is(malariaProgramUid))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent"
     */
    public static List<SurveyDB> getAllSurveysToBeSent() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys
     */
    public static List<SurveyDB> getAllSurveys() {
        return new Select().from(SurveyDB.class)
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.completion_date)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Sent"
     */
    public static List<SurveyDB> getSentSurveys(int limit) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENT))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Completed"
     */
    public static List<SurveyDB> getAllCompletedSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    public static List<SurveyDB> getAllCompletedSurveysNoReceiptReset() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_COMPLETED))
                .and(SurveyDB_Table.type.isNot(Constants.SURVEY_RECEIPT))
                .and(SurveyDB_Table.type.isNot(Constants.SURVEY_RESET))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "Completed"
     */
    public static List<SurveyDB> getCompletedSurveys(int limit) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_COMPLETED))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "In progress"
     */
    public static List<SurveyDB> getAllUncompletedSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.is(Constants.SURVEY_IN_PROGRESS))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status put to "In progress"
     */
    public static List<SurveyDB> getUncompletedSurveys(int limit) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_IN_PROGRESS))
                .limit(limit)
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<SurveyDB> getAllUncompletedUnsentSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.isNot(Constants.SURVEY_COMPLETED))
                .or(SurveyDB_Table.status.isNot(Constants.SURVEY_SENT))
                .or(SurveyDB_Table.status.isNot(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns the last surveys (by date) with status Completed or sent
     */
    public static List<SurveyDB> getAllCompletedUnsentSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.is(Constants.SURVEY_COMPLETED))
                .or(SurveyDB_Table.status.isNot(Constants.SURVEY_SENT))
                .or(SurveyDB_Table.status.isNot(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    /**
     * Returns all the surveys with status put to "Sent" or completed
     */
    public static List<SurveyDB> getAllSentOrCompletedSurveys() {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENT))
                .or(SurveyDB_Table.status.eq(Constants.SURVEY_COMPLETED))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    // Returns all the surveys with status put to "Hide"
    public static List<SurveyDB> getAllHideAndSentSurveys(int limit) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENT))
                .limit(limit)
                .or(SurveyDB_Table.status.eq(Constants.SURVEY_HIDE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.id_org_unit_fk)).queryList();
    }

    public static void removeInProgress() {
        List<SurveyDB> inProgressSurveyDB = getAllUncompletedSurveys();
        for (int i = inProgressSurveyDB.size() - 1; i >= 0; i--) {
            inProgressSurveyDB.get(i).delete();
        }
    }

    /**
     * Find the surveys that have been sent after the given date
     */
    public static List<SurveyDB> findSentSurveysAfterDate(Date minDateForMonitor) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENT))
                .or(SurveyDB_Table.status.eq(Constants.SURVEY_CONFLICT))
                .and(SurveyDB_Table.event_date.greaterThanOrEq(
                        minDateForMonitor)).queryList();
    }

    /**
     * Find the surveys for a mProgramDB, with a type o no type ans with the event date grater than
     * passed.
     *
     * @param programDB The mProgramDB of the survey
     * @param date    The min eventDate of the survey
     * @return A list of surveys
     */
    public static List<SurveyDB> findSurveysWithProgramAndGreaterDate(ProgramDB programDB,
            Date date) {
        return new Select().from(SurveyDB.class).as(surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .where(ProgramDB_Table.id_program.withTable(programAlias)
                        .eq(programDB.getId_program()))
                .and(SurveyDB_Table.event_date.withTable(surveyAlias).greaterThanOrEq(
                        date)).queryList();
    }

    public static Date getLastDateForSurveyType(int type) {
        SurveyDB surveyDB = new Select().
                from(SurveyDB.class)
                .where(SurveyDB_Table.type.eq(type))
                .groupBy(SurveyDB_Table.id_survey)
                .having(SurveyDB_Table.event_date.is(Method.max(SurveyDB_Table.event_date)))
                .querySingle();
        if (surveyDB == null) {
            return new Date(0);
        }
        return surveyDB.getEventDate();
    }

    public static SurveyDB getLastSurveyWithType(int type) {
        SurveyDB surveyDB = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.type.eq(type))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending())
                .querySingle();
        return surveyDB;
    }


    public static List<SurveyDB> getSurveysWithProgramType(ProgramDB programDB, int type) {
        return new Select().from(SurveyDB.class).as(surveyName)
                .join(ProgramDB.class, Join.JoinType.LEFT_OUTER).as(programName)
                .on(SurveyDB_Table.id_program_fk.withTable(surveyAlias)
                        .eq(ProgramDB_Table.id_program.withTable(programAlias)))
                .where(ProgramDB_Table.id_program.withTable(programAlias)
                        .eq(programDB.getId_program()))
                .and(SurveyDB_Table.type.withTable(surveyAlias).is(type)).queryList();
    }

    /**
     * Finds a survey by its ID
     */
    public static SurveyDB findById(Long id_survey) {
        return new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.id_survey
                        .eq(id_survey))
                .querySingle();
    }

    public static void removeValue(ValueDB valueDB) {
        valueDB.delete();
    }

    public static int countSurveysByCompletiondate(Date completion_date) {

        return (int) SQLite.selectCountOf()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.completion_date.eq(completion_date))
                .count();
    }

    public static Date getMinQuarantineEventDate() {
        SurveyDB surveyDB = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).ascending())
                .querySingle();
        return surveyDB.getEventDate();
    }

    public static Date getMaxQuarantineEventDate() {
        SurveyDB surveyDB = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending())
                .querySingle();
        return surveyDB.getEventDate();
    }

    public static List<SurveyDB> getAllSendingSurveys() {
        return new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_SENDING))
                .queryList();
    }

    public Float getCounterValue(QuestionDB questionDB, OptionDB selectedOptionDB) {
        QuestionDB optionCounter = questionDB.findCounterByOption(selectedOptionDB);
        if (optionCounter == null) {
            return 0f;
        }
        String counterValue = optionCounter.getQuestionValueBySession();
        if (counterValue == null || counterValue.isEmpty()) {
            return 0f;
        }
        return Float.parseFloat(counterValue);
    }

    public HashMap<TabDB, Integer> getAnsweredTabs() {
        HashMap<TabDB, Integer> tabs = new HashMap<TabDB, Integer>();
        List<ValueDB> valueDBs = getValuesFromDB();
        int tabSize = 0;
        for (ValueDB valueDB : valueDBs) {
            TabDB tabDB = valueDB.getQuestionDB().getHeaderDB().getTabDB();
            if (!tabs.containsKey(tabDB)) {
                tabs.put(tabDB, tabSize);
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
        SurveyDB srv = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.id_survey.eq(idSurvey)).querySingle();

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
            ScoreDB scoreDB = getScore();
            this.mainScore = (scoreDB == null) ? 0f : scoreDB.getScore();
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
        ScoreDB scoreDB = new ScoreDB(this, "", valScore);
        scoreDB.save();
    }

    private ScoreDB getScore() {
        return new Select()
                .from(ScoreDB.class)
                .where(ScoreDB_Table.id_survey_fk.eq(
                        this.getId_survey())).querySingle();
    }

    @Override
    public void delete() {
        ScoreDB scoreDB = getScore();
        if (scoreDB != null) {
            scoreDB.delete();
        }
        for (ValueDB valueDB : getValuesFromDB()) {
            valueDB.delete();
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
    public List<ValueDB> getValueDBs() {
        if (mValueDBs == null) {
            mValueDBs = new Select()
                    .from(ValueDB.class)
                    .where(ValueDB_Table.id_survey_fk
                            .eq(this.getId_survey())).queryList();
        }
        return mValueDBs;
    }

    /**
     * Returns the list of answered values from this survey
     */
    public List<ValueDB> getValuesFromDB() {
        mValueDBs = new Select()
                .from(ValueDB.class)
                .where(ValueDB_Table.id_survey_fk
                        .eq(this.getId_survey())).queryList();
        return mValueDBs;
    }


    /**
     * Returns the list of answered values from this survey
     */
    public List<ValueDB> getValuesFromDBWithoutSave() {
        return new Select()
                .from(ValueDB.class)
                .where(ValueDB_Table.id_survey_fk
                        .eq(this.getId_survey())).queryList();
    }
    /**
     * Returns the list of mQuestionDBs from answered values
     */
    public List<QuestionDB> getQuestionsFromValues() {
        List<QuestionDB> questionDBs = new Select()
                .from(QuestionDB.class).as(questionName)
                .join(ValueDB.class, Join.JoinType.LEFT_OUTER).as(valueName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                        .eq(QuestionDB_Table.id_question.withTable(questionAlias)))
                .where(ValueDB_Table.id_survey_fk.withTable(valueAlias)
                        .eq(this.getId_survey()))
                .orderBy(OrderBy.fromProperty(QuestionDB_Table.order_pos).ascending()).queryList();
        return questionDBs;
    }

    /**
     * Returns the list of previous schedules for this survey
     */
    public List<SurveyScheduleDB> getSurveyScheduleDBs() {
        if (mSurveyScheduleDBs == null) {
            mSurveyScheduleDBs = new Select()
                    .from(SurveyScheduleDB.class)
                    .where(SurveyScheduleDB_Table.id_survey_fk
                            .eq(this.getId_survey())).queryList();
        }
        return mSurveyScheduleDBs;
    }

    /**
     * Returns the list of answered values from this survey that belong to a parent mQuestionDB
     */
    public List<ValueDB> getValuesFromParentQuestions() {
        List<ValueDB> valueDBs = new Select().from(ValueDB.class).as(valueName)
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                        .eq(QuestionDB_Table.id_question.withTable(questionAlias)))
                .where(ValueDB_Table.id_survey_fk.withTable(valueAlias)
                        .eq(this.getId_survey()))
                .and(QuestionDB_Table.id_question_parent.withTable(questionAlias).isNull())
                .and(ValueDB_Table.value.withTable(valueAlias).isNotNull())
                .and(ValueDB_Table.value.withTable(valueAlias).isNot(
                        "")).queryList();
        //List<ValueDB> values = ValueDB.findWithQuery(ValueDB.class, LIST_VALUES_PARENT_QUESTION, this
        // .getId().toString());
        return valueDBs;
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
     * @return SurveyAnsweredRatio that hold the total & answered mQuestionDBs.
     */
    public SurveyAnsweredRatio reloadSurveyAnsweredRatio() {
        SurveyAnsweredRatio surveyAnsweredRatio;
        //First parent is always required and not calculated.
        int numRequired = 1;
        int numAnswered = 0;

        IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
        ProgramDB programDB = ProgramDB.findByUID(programLocalDataSource.getUserProgram().getId());
        TabDB tabDB = programDB.getTabDBs().get(0);
        QuestionDB rootQuestionDB = QuestionDB.findRootQuestion(tabDB);
        QuestionDB localQuestionDB = rootQuestionDB;
        numRequired = SurveyFragmentStrategy.getNumRequired(numRequired, localQuestionDB);

        //Add children required by each parent (value+mQuestionDB)
        SurveyDB surveyDB = SurveyDB.findById(id_survey);
        for (ValueDB valueDB : surveyDB.getValuesFromDB()) {
            if (valueDB.getQuestionDB().isCompulsory() && valueDB.getId_option() != null) {
                numRequired += QuestionDB.countChildrenByOptionValue(valueDB.getId_option());
            }
        }
        numAnswered += countCompulsoryBySurvey(this);
        Log.d("survey answered", "num required: " + numRequired + " num answered: " + numAnswered);
        surveyAnsweredRatio = new SurveyAnsweredRatio(numRequired, numAnswered);

        SurveyAnsweredRatioCache.put(this.id_survey, surveyAnsweredRatio);
        return surveyAnsweredRatio;
    }


    public static int countCompulsoryBySurvey(SurveyDB surveyDB) {
        List<QuestionDB> questionDBs = surveyDB.getQuestionsFromValues();
        int countOfCompulsoryValues = 0;
        for (QuestionDB questionDB : questionDBs) {
            if (questionDB.isCompulsory()) {
                countOfCompulsoryValues++;
            }
        }
        return countOfCompulsoryValues;
    }

    /**
     * Return the number of optional mQuestionDBs like a counter by survey
     */
    private long countNumOptionalQuestionsAnswered() {
        long numOptionalQuestions = SQLite.selectCountOf().from(QuestionOptionDB.class).as(
                questionOptionName)
                .join(MatchDB.class, Join.JoinType.INNER).as(matchName)
                .on(MatchDB_Table.id_match.withTable(matchAlias)
                        .eq(QuestionOptionDB_Table.id_match_fk.withTable(questionOptionAlias)))

                .join(QuestionRelationDB.class, Join.JoinType.INNER).as(questionRelationName)
                .on(QuestionRelationDB_Table.id_question_relation.withTable(questionRelationAlias)
                        .eq(MatchDB_Table.id_question_relation_fk.withTable(matchAlias)))
                .join(ValueDB.class, Join.JoinType.INNER).as(valueName)
                .on(ValueDB_Table.id_question_fk.withTable(valueAlias)
                        .eq(QuestionRelationDB_Table.id_question_fk))
                .join(QuestionDB.class, Join.JoinType.INNER).as(questionName)
                .on(QuestionDB_Table.id_question.withTable(questionAlias)
                        .eq(QuestionRelationDB_Table.id_question_fk.withTable(
                                questionRelationAlias)))
                //Type of mQuestionDB-> Counter
                .where(QuestionDB_Table.output.eq(
                        Constants.COUNTER))
                //For the given survey
                .and(ValueDB_Table.id_survey_fk.eq(
                        this.getId_survey()))
                .count();

        //Parent with the right value -> not hidden
        return numOptionalQuestions;
    }

    /**
     * Updates ratios, status and completion date depending on the mQuestionDB and mAnswerDB (text)
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
     * Checks if the mAnswerDB to the first mQuestionDB is 'Yes'
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
     * Since there are three possible values first mQuestionDB (RDT):'Positive','Negative','Not
     * Tested'
     *
     * @return String
     */
    public String getRDTName() {
        String rdtValue = "";
        if (mValueDBs == null) {
            mValueDBs = ValueDB.listAllBySurvey(this);
        }

        if (mValueDBs.size() > 0) {
            for (ValueDB valueDB : mValueDBs) {
                //Find the RTS mOptionDB
                if (valueDB.getOptionDB() != null && valueDB.getQuestionDB() != null
                        && valueDB.getQuestionDB().getCode().equals(
                        PreferencesState.getInstance().getContext().getString(R.string.RDT_code))) {
                    rdtValue = valueDB.getOptionDB().getCode();
                }
            }

        }
        return rdtValue;
    }

    /**
     * Since there are three possible values first mQuestionDB (RDT):'Positive','Negative','Not
     * Tested'
     *
     * @return String
     */
    public String getResultCode() {
        String rdtValue = "";
        if (mValueDBs == null) {
            mValueDBs = ValueDB.listAllBySurvey(this);
        }

        if (mValueDBs.size() > 0) {
            for (ValueDB valueDB : mValueDBs) {
                //Find the RTS mOptionDB
                if (valueDB.getOptionDB() != null && valueDB.getQuestionDB() != null
                        && valueDB.getQuestionDB().getCode().equals(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.Result_code))) {
                    rdtValue = valueDB.getOptionDB().getInternationalizedName();
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
        if (mValueDBs == null) {
            mValueDBs = ValueDB.listAllBySurvey(this);
        }

        Iterator<ValueDB> iterator = mValueDBs.iterator();

        String valuesStr = "";

        //Define a filter to select which values will be turned into string by code_question
        List<QuestionDB> questionDBs = new ArrayList<>();
        for (ValueDB valueDB : mValueDBs) {
            questionDBs.add(valueDB.getQuestionDB());
        }
        List<String> codeQuestionFilter = new ArrayList<String>();

        for (QuestionDB questionDB : questionDBs) {
            if (questionDB != null && questionDB.isVisible()) {
                codeQuestionFilter.add(questionDB.getCode());
            }
        }

        Map map = new HashMap();
        while (iterator.hasNext()) {
            ValueDB valueDB = iterator.next();
            //The control dataelements not have mQuestionDBs and its should be ignored
            if (valueDB.getQuestionDB() == null || valueDB.getValue() == null) {
                continue;
            }
            String qCode = valueDB.getQuestionDB().getCode();

            if (codeQuestionFilter.contains(qCode)) {
                String val =
                        (valueDB.getOptionDB() != null) ? valueDB.getOptionDB().getInternationalizedName()
                                : valueDB.getValue();
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
            for (ValueDB valueDB : mValueDBs) {
                valuesString += "ValueDB: " + valueDB.getValue();
                if (valueDB.getOptionDB() != null) {
                    valuesString += " OptionDB: " + valueDB.getOptionDB().getInternationalizedCode();
                }
                if (valueDB.getQuestionDB() != null) {
                    valuesString += " Question: " + valueDB.getQuestionDB().getDe_name() + "\n";
                }
            }
        }
        return valuesString;
    }

    /**
     * This method removes the children mQuestionDB values from when a parent mQuestionDB is removed
     *
     * @removeCounters is used to prevent to remove the counter when change the some mQuestionDB in
     * the
     * same level
     * This changed was required because the counter need be child of the mQuestionDB+mOptionDB who
     * tigger
     * the counter.
     */
    public void removeChildrenValuesFromQuestionRecursively(QuestionDB questionDB,
            boolean removeCounters) {
        List<ValueDB> valueDBs = getValuesFromDB();
        List<QuestionDB> questionDBChildren = questionDB.getChildren();
        for (int i = valueDBs.size() - 1; i > 0; i--) {
            //This loop removes recursively the values on the children mQuestionDB
            if (questionDBChildren.contains(valueDBs.get(i).getQuestionDB())) {
                //Remove the children values but if the child value is a counter is not removed
                // in the first level
                if (!valueDBs.get(i).getQuestionDB().isACounter() || removeCounters) {
                    for (QuestionDB questionDBPropagated : valueDBs.get(
                            i).getQuestionDB().getPropagationQuestions()) {
                        removeValue(questionDBPropagated.getValueBySurvey(
                                Session.getMalariaSurveyDB()));
                    }
                    removeValue(valueDBs.get(i));
                }
                for (QuestionDB child : questionDBChildren) {
                    removeChildrenValuesFromQuestionRecursively(child, true);
                }
            }
        }
    }


    public static List<OrgUnitDB> getQuarantineOrgUnits(long programUid) {
        return new Select().from(OrgUnitDB.class) .as(orgUnitName)
                .join(SurveyDB.class, Join.JoinType.LEFT_OUTER).as(surveyName)
                .on(SurveyDB_Table.id_org_unit_fk.withTable(surveyAlias)
                        .eq(OrgUnitDB_Table.id_org_unit.withTable(orgUnitAlias)))
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(SurveyDB_Table.id_program_fk.eq(programUid)).queryList();
    }

    public static List<SurveyDB> getAllQuarantineSurveysByProgramAndOrgUnit(ProgramDB programDB,
            OrgUnitDB orgUnitDB) {
        return new Select().from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(SurveyDB_Table.id_program_fk.eq(programDB.getId_program()))
                .and(SurveyDB_Table.id_org_unit_fk.eq(orgUnitDB.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending()).queryList();
    }

    public static Date getMinQuarantineCompletionDateByProgramAndOrgUnit(ProgramDB programDB,
            OrgUnitDB orgUnitDB) {
        SurveyDB surveyDB = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(SurveyDB_Table.id_program_fk.eq(programDB.getId_program()))
                .and(SurveyDB_Table.id_org_unit_fk.eq(orgUnitDB.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.completion_date).ascending())
                .querySingle();
        return surveyDB.getCompletionDate();
    }

    public static Date getMaxQuarantineEventDateByProgramAndOrgUnit(ProgramDB programDB,
            OrgUnitDB orgUnitDB) {
        SurveyDB surveyDB = new Select()
                .from(SurveyDB.class)
                .where(SurveyDB_Table.status.eq(Constants.SURVEY_QUARANTINE))
                .and(SurveyDB_Table.id_program_fk.eq(programDB.getId_program()))
                .and(SurveyDB_Table.id_org_unit_fk.eq(orgUnitDB.getId_org_unit()))
                .orderBy(OrderBy.fromProperty(SurveyDB_Table.event_date).descending())
                .querySingle();
        return surveyDB.getEventDate();
    }

    public static void deleteAll() {
        List<SurveyDB> surveyDBs = SurveyDB.getAllSurveys();
        for (SurveyDB surveyDB : surveyDBs) {
            surveyDB.delete();
        }
    }
    /**
     * This method get the return the highest number of total pages in the survey mQuestionDB values.
     */
    public int getMaxTotalPages() {
        List<ValueDB> valueDBs = getValuesFromDB();
        int totalPages = 0;
        for (ValueDB valueDB : valueDBs) {
            if (valueDB.getQuestionDB() != null
                    && valueDB.getQuestionDB().getTotalQuestions() > totalPages) {
                totalPages = valueDB.getQuestionDB().getTotalQuestions();
            }
        }
        return totalPages;
    }

    @Override
    public void accept(IConvertToSDKVisitor IConvertToSDKVisitor) throws ConversionException {
        IConvertToSDKVisitor.visit(this);
    }

    public static void saveAll(List<SurveyDB> surveyDBs, final IDataSourceCallback<Void> callback) {

        FlowManager.getDatabase(AppDatabase.class)
                .beginTransactionAsync(new ProcessModelTransaction.Builder<>(
                        new ProcessModelTransaction.ProcessModel<SurveyDB>() {
                            @Override
                            public void processModel(SurveyDB surveyDB) {
                                surveyDB.save();
                            }
                        }).addAll(surveyDBs).build())
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

        SurveyDB surveyDB = (SurveyDB) o;

        if (id_survey != surveyDB.id_survey) return false;
        if (id_program_fk != null ? !id_program_fk.equals(surveyDB.id_program_fk)
                : surveyDB.id_program_fk != null) {
            return false;
        }
        if (id_org_unit_fk != null ? !id_org_unit_fk.equals(surveyDB.id_org_unit_fk)
                : surveyDB.id_org_unit_fk != null) {
            return false;
        }
        if (id_user_fk != null ? !id_user_fk.equals(surveyDB.id_user_fk)
                : surveyDB.id_user_fk != null) {
            return false;
        }
        if (creation_date != null ? !creation_date.equals(surveyDB.creation_date)
                : surveyDB.creation_date != null) {
            return false;
        }
        if (completion_date != null ? !completion_date.equals(surveyDB.completion_date)
                : surveyDB.completion_date != null) {
            return false;
        }
        if (event_date != null ? !event_date.equals(surveyDB.event_date)
                : surveyDB.event_date != null) {
            return false;
        }
        if (scheduled_date != null ? !scheduled_date.equals(surveyDB.scheduled_date)
                : surveyDB.scheduled_date != null) {
            return false;
        }
        if (status != null ? !status.equals(surveyDB.status) : surveyDB.status != null) {
            return false;
        }
        return type != null ? type.equals(surveyDB.type) : surveyDB.type == null;

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
