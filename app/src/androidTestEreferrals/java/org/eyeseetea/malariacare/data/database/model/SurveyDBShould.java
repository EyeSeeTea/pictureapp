package org.eyeseetea.malariacare.data.database.model;

import static org.junit.Assert.assertTrue;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.utils.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class SurveyDBShould {

    private long idSentSurveyWithTodayEventDate;
    private long idSentSurveyWith25AfterTodayEventDate;

    @Before
    public void setup() {
        SurveyDB.deleteAll();
    }

    @After
    public void after() {
        SurveyDB.deleteAll();
        idSentSurveyWithTodayEventDate = 0;
        idSentSurveyWith25AfterTodayEventDate = 0;
    }

    @Test
    public void delete_all_sent_surveys_older_than_2_weeks() {

        whenAddASentSurveyWithEventDate25AfterToday();

        thenAssertSentSurveyWithEventDateOlderThan2WeeksWasInTheDB();

        thenDelete2WeeksOldSurveys();

        thenAssertSentSurveyWithEventDate25DaysAfterTodayWasNotInDB();
    }

    @Test
    public void delete_only_sent_surveys_older_than_2_weeks() {

        whenAddSentSurveyWithTodayEventDate();

        thenAssertSentSurveyWithTodayEventDateWasInTheDB();

        thenDelete2WeeksOldSurveys();

        thenAssertSentSurveyWithTodayEventDateWasInTheDB();

        whenAddASentSurveyWithEventDate25AfterToday();

        thenDelete2WeeksOldSurveys();

        thenAssertSentSurveyWithEventDate25DaysAfterTodayWasNotInDB();

        thenAssertSentSurveyWithTodayEventDateWasInTheDB();
    }

    @Test
    public void keep_sent_surveys_not_older_than_2_weeks() {

        whenAddSentSurveyWithTodayEventDate();

        thenAssertSentSurveyWithTodayEventDateWasInTheDB();

        thenDelete2WeeksOldSurveys();

        thenAssertSentSurveyWithTodayEventDateWasInTheDB();

    }

    private void thenAssertSentSurveyWithEventDate25DaysAfterTodayWasNotInDB() {
        SurveyDB surveyDB = getSurveyByID(idSentSurveyWith25AfterTodayEventDate);
        assertTrue(surveyDB == null);
    }

    private void whenAddASentSurveyWithEventDate25AfterToday() {
        Date today = new Date();
        Date todayWith25Days = SurveyDB.minusDaysTo(today, 25);

        idSentSurveyWith25AfterTodayEventDate = createANewSentSurvey(todayWith25Days);
    }

    private void thenDelete2WeeksOldSurveys() {
        SurveyDB.deleteOlderSentSurveys(14);
    }

    private void whenAddSentSurveyWithTodayEventDate() {
        Date today = new Date();
        idSentSurveyWithTodayEventDate = createANewSentSurvey(today);
    }

    private void thenAssertSentSurveyWithEventDateOlderThan2WeeksWasInTheDB() {
        SurveyDB surveyDB = getSurveyByID(idSentSurveyWith25AfterTodayEventDate);
        assertTrue(surveyDB != null);
    }

    private void thenAssertSentSurveyWithTodayEventDateWasInTheDB() {
        SurveyDB surveyDB = getSurveyByID(idSentSurveyWithTodayEventDate);
        assertTrue(surveyDB != null);
    }


    private long createANewSentSurvey(Date eventDate) {
        SurveyDB surveyDB = new SurveyDB();
        surveyDB.setProgramDB(1l);
        surveyDB.setEventDate(eventDate);
        surveyDB.setStatus(Constants.SURVEY_SENT);
        surveyDB.save();
        return surveyDB.getId_survey();
    }

    private SurveyDB getSurveyByID(long id) {
        return new Select().from(SurveyDB.class).where(
                SurveyDB_Table.id_survey.eq(id)).querySingle();
    }


}