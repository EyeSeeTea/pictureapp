/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Survelliance App.
 *
 *  QIS Survelliance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Survelliance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Survelliance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.database.model;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyAction;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.builder.Condition;
import com.raizlabs.android.dbflow.sql.language.ColumnAlias;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.database.AppDatabase;
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
public class Survey extends BaseModel {

//    public static final float MAX_AMBER = 80f;
//    public static final float MAX_RED = 50f;

    @Column
    @PrimaryKey(autoincrement = true)
    long id_survey;
    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "id_org_unit",
            columnType = Long.class,
            foreignColumnName = "id_org_unit")},
            saveForeignKeyModel = false)
    OrgUnit orgUnit;
    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "id_program",
            columnType = Long.class,
            foreignColumnName = "id_program")},
            saveForeignKeyModel = false)
    Program program;
    @Column
    @ForeignKey(
            references = {@ForeignKeyReference(columnName = "id_user",
            columnType = Long.class,
            foreignColumnName = "id_user")},
            saveForeignKeyModel = false)
    User user;
    @Column
    Date eventDate;
    @Column
    Date completionDate;
    @Column
    Integer status;

    SurveyAnsweredRatio answeredQuestionRatio;

    List<Value> values;

    public Survey() {
    }

    public Survey(OrgUnit orgUnit, Program program, User user) {
        this.orgUnit = orgUnit;
        this.program = program;
        this.user = user;
        this.eventDate = new Date();
        this.status = Constants.SURVEY_IN_PROGRESS; // Possibilities [ In progress | Completed | Sent ]
        this.completionDate= this.eventDate;

        Log.i(".Survey", Long.valueOf(this.completionDate.getTime()).toString());
    }

    public Long getId_survey() {
        return id_survey;
    }

    public void setId_survey(Long id_survey) {
        this.id_survey = id_survey;
    }

    public OrgUnit getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(OrgUnit orgUnit) {
        this.orgUnit = orgUnit;
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Date getCompletionDate(){
        return completionDate;
    }

    public void setCompletionDate(Date completionDate){
        this.completionDate=completionDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Checks if the survey has been sent or not
     * @return true|false
     */
    public boolean isSent(){
        return Constants.SURVEY_SENT==this.status;
    }

    /**
     * Checks if the survey has been hidden or not
     * @return true|false
     */
    public boolean isHide(){
        return Constants.SURVEY_HIDE==this.status;
    }

    /**
     * Checks if the survey has been completed or not
     * @return true|false
     */
    public boolean isCompleted(){
        return Constants.SURVEY_COMPLETED==this.status;
    }

    /**
     * Checks if the survey is in progress
     * @return true|false
     */
    public boolean isInProgress(){
        return !isSent() && !isCompleted()&& !isHide();
    }


    /**
     * Returns the list of answered values from this survey
     * @return
     */
    @OneToMany(methods = {OneToMany.Method.SAVE, OneToMany.Method.DELETE}, variableName = "values")
    public List<Value> getValues(){
        return new Select().from(Value.class)
                .where(Condition.column(Value$Table.SURVEY_ID_SURVEY).eq(this.getId_survey())).queryList();
    }

    //public List<Value> getValues(){
    //    return new Select().from(Value.class)
    //           .where(Condition.column(Value$Table.SURVEY_ID_SURVEY).eq(this.getId_survey())).queryList();
    //}

    /**
     * Returns the list of answered values from this survey that belong to a parent question
     * @return
     */
    public List<Value> getValuesFromParentQuestions(){

        List<Value> values = new Select().all().from(Value.class).as("v")
                .join(Question.class, Join.JoinType.LEFT).as("q")
                .on(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.QUESTION_ID_QUESTION))
                        .eq(ColumnAlias.columnWithTable("q", Question$Table.ID_QUESTION)))
                .where(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.SURVEY_ID_SURVEY))
                        .eq(this.getId_survey()))
                .and(Condition.column(ColumnAlias.columnWithTable("q", Question$Table.QUESTION_ID_PARENT)).isNull())
                .and(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.VALUE)).isNotNull())
                .and(Condition.column(ColumnAlias.columnWithTable("v", Value$Table.VALUE)).isNot("")).queryList();
        //List<Value> values = Value.findWithQuery(Value.class, LISTvalues_PARENT_QUESTION, this.getId().toString());
        return values;
    }

    /**
     * Ratio of completion is cached into answeredQuestionRatio in order to speed up loading
     * @return
     */
    public SurveyAnsweredRatio getAnsweredQuestionRatio(){
        if (answeredQuestionRatio == null) {
            answeredQuestionRatio=SurveyAnsweredRatioCache.get(this.getId_survey());
            if(answeredQuestionRatio == null) {
                answeredQuestionRatio = reloadSurveyAnsweredRatio();
            }
        }
        return answeredQuestionRatio;
    }

    /**
     * Calculates the current ratio of completion for this survey
     * @return SurveyAnsweredRatio that hold the total & answered questions.
     */
    private SurveyAnsweredRatio reloadSurveyAnsweredRatio(){

        int numRequired= Question.countRequiredByProgram(this.getProgram());
        int numOptional=0;
        int numAnswered = Value.countBySurvey(this);

        for (Value value : this.getValuesFromParentQuestions()) {
            if (value.isAPositive()) {
                //There might be children no answer questions that should be skipped
                for(Question childQuestion:value.getQuestion().getQuestionChildren()){
                    numOptional+=(childQuestion.getAnswer().getOutput()==Constants.NO_ANSWER)?0:1;
                }
            }

        }
        SurveyAnsweredRatio surveyAnsweredRatio=new SurveyAnsweredRatio(numRequired+numOptional, numAnswered);
        SurveyAnsweredRatioCache.put(this.getId_survey(), surveyAnsweredRatio);
        return surveyAnsweredRatio;
    }

    /**
     * Updates ratios, status and completion date depending on the question and answer (text)
     */
    public void updateSurveyStatus(){

        //Sent surveys are not updated
        if(this.isSent()){
            return;
        }

        SurveyAnsweredRatio answeredRatio=this.reloadSurveyAnsweredRatio();

        //Update status & completionDate
        //Temporal fix- If the survey is negative, it is interpreted that it was false .:
        if(answeredRatio.isCompleted() || answeredRatio.getAnswered()==1) {
            this.setStatus(Constants.SURVEY_COMPLETED);
            this.setCompletionDate(new Date());
        }else {
            this.setStatus(Constants.SURVEY_IN_PROGRESS);
            this.setCompletionDate(this.eventDate);
        }

        //Saves new status & completionDate
        this.save();
    }

    // Returns a concrete survey, if it exists
    public static List<Survey> getUnsentSurveys(OrgUnit orgUnit, Program program) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.ORGUNIT_ID_ORG_UNIT).eq(orgUnit.getId_org_unit()))
                .and(Condition.column(Survey$Table.PROGRAM_ID_PROGRAM).eq(program.getId_program()))
                .and(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .and(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }

    // Returns all the surveys with status yet not put to "Sent"
    public static List<Survey> getAllUnsentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .and(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }

    // Returns the last surveys (by date) with status yet not put to "Sent"
    public static List<Survey> getUnsentSurveys(int limit) {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_SENT))
                .and(Condition.column(Survey$Table.STATUS).isNot(Constants.SURVEY_HIDE))
                .limit(String.valueOf(limit))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }

    // Returns all the surveys with status put to "Sent"
    public static List<Survey> getAllSentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }

    // Returns all the surveys with status put to "Hide"
    public static List<Survey> getAllHideAndSentSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_SENT))
                .or(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_HIDE))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT)
                .queryList();
    }


    // Returns all the surveys with status put to "Completed"
    public static List<Survey> getAllCompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_COMPLETED))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }



    // Returns all the surveys with status put to "In progress"
    public static List<Survey> getAllUncompletedSurveys() {
        return new Select().from(Survey.class)
                .where(Condition.column(Survey$Table.STATUS).eq(Constants.SURVEY_IN_PROGRESS))
                .orderBy(Survey$Table.EVENTDATE)
                .orderBy(Survey$Table.ORGUNIT_ID_ORG_UNIT).queryList();
    }


    /**
     * Checks if the answer to the first question is 'Yes'
     * @return true|false
     */
    public boolean isRDT(){
        if(values==null){
            values=Value.listAllBySurvey(this);
        }

        if(values.size()==0){
            return false;
        }

        Value rdtValue=values.get(0);

        return rdtValue.isAPositive();
    }

    /**
     * Since there are three possible values first question (RDT):'Positive','Negative','Not Tested'
     * @return String
     */
    public String getRDT() {
        String rdtValue = "";
        if (values == null) {
            values = Value.listAllBySurvey(this);
        }

        if (values.size() > 0) {
            Value firstValue = values.get(0);
            rdtValue = firstValue.getOption().getName();
        }
        return rdtValue;
    }

    /**
     * Turns all values from a survey into a string with values separated by commas
     * @return String
     */
    public String getValuesToString(){
        if(values==null || values.size()==0){
            return "";
        }

        Iterator<Value> iterator=values.iterator();

        String valuesStr="";
        boolean valid = true;

        //Define a filter to select which values will be turned into string by code_question
        List<String> codeQuestionFilter = new ArrayList<String>() {{
            add("Specie");  //4
            add("Sex");     //2
            add("Age");     //3
        }};

        Map mapa = new HashMap();
        while(iterator.hasNext() && valid){
            Value value = iterator.next();
            String qCode = value.getQuestion().getCode();

            // RDT is the first field: if it is not Positive no values are shown
            if(("RDT").equals(qCode) && !value.isAPositive()){
                valid = false;
            }else{
                if(codeQuestionFilter.contains(qCode)) {
                    String val = (value.getOption()!=null)?value.getOption().getCode():value.getValue();
                    mapa.put(qCode, val);
                }
            }
        }

        if(valid) {
            //Sort values
            for(int i=0; i<codeQuestionFilter.size(); i++){
                valuesStr += mapa.get(codeQuestionFilter.get(i));
                if (i < codeQuestionFilter.size()-1) {
                    valuesStr += ", ";
                }
            }
        }

        return valuesStr;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Survey)) return false;

        Survey survey = (Survey) o;

        if (answeredQuestionRatio != null ? !answeredQuestionRatio.equals(survey.answeredQuestionRatio) : survey.answeredQuestionRatio != null)
            return false;
        if (!eventDate.equals(survey.eventDate)) return false;
        if (!completionDate.equals(survey.completionDate)) return false;
        if (!orgUnit.equals(survey.orgUnit)) return false;
        if (!program.equals(survey.program)) return false;
        if (!status.equals(survey.status)) return false;
        if (!user.equals(survey.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = orgUnit.hashCode();
        result = 31 * result + program.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + eventDate.hashCode();
        result = 31 * result + completionDate.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + (answeredQuestionRatio != null ? answeredQuestionRatio.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "orgUnit=" + orgUnit +
                ", program=" + program +
                ", user=" + user +
                ", eventDate=" + eventDate +
                ", completionDate=" + completionDate +
                ", status=" + status +
                '}';
    }

    public static void removeInProgress() {
        List<Survey> inProgressSurvey= getAllUncompletedSurveys();
        for(int i=inProgressSurvey.size()-1;i>=0;i--){
            inProgressSurvey.get(i).delete();
        }
    }
}
