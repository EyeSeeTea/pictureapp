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

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

/**
 * Created by adrian on 14/02/15.
 */
@Table(database = AppDatabase.class, name = "Score")
public class ScoreDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_score;

    @Column
    Long id_survey_fk;
    /**
     * Reference to the survey associated to this score (loaded lazily)
     */
    SurveyDB mSurveyDB;

    @Column
    String uid_score;

    @Column
    Float score;

    public ScoreDB() {
    }

    public ScoreDB(SurveyDB surveyDB, String uid, Float score) {
        this.uid_score = uid;
        this.score = score;
        this.setSurveyDB(surveyDB);
    }

    public Long getId_score() {
        return id_score;
    }

    public void setId_score(Long id_score) {
        this.id_score = id_score;
    }

    public SurveyDB getSurveyDB() {
        if (mSurveyDB == null) {
            if (id_survey_fk == null) return null;
            mSurveyDB = new Select()
                    .from(SurveyDB.class)
                    .where(SurveyDB_Table.id_survey
                            .is(id_survey_fk)).querySingle();
        }
        return mSurveyDB;
    }

    public void setSurveyDB(SurveyDB surveyDB) {
        this.mSurveyDB = surveyDB;
        this.id_survey_fk = (surveyDB != null) ? surveyDB.getId_survey() : null;
    }

    public void setSurvey(Long id_survey) {
        this.id_survey_fk = id_survey;
        this.mSurveyDB = null;
    }

    public String getUid() {
        return uid_score;
    }

    public void setUid(String uid) {
        this.uid_score = uid;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreDB scoreDB1 = (ScoreDB) o;

        if (id_score != scoreDB1.id_score) return false;
        if (id_survey_fk != null ? !id_survey_fk.equals(scoreDB1.id_survey_fk)
                : scoreDB1.id_survey_fk != null) {
            return false;
        }
        if (uid_score != null ? !uid_score.equals(scoreDB1.uid_score)
                : scoreDB1.uid_score != null) {
            return false;
        }
        return !(score != null ? !score.equals(scoreDB1.score) : scoreDB1.score != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (id_score ^ (id_score >>> 32));
        result = 31 * result + (id_survey_fk != null ? id_survey_fk.hashCode() : 0);
        result = 31 * result + (uid_score != null ? uid_score.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ScoreDB{" +
                "id_score=" + id_score +
                ", id_survey_fk=" + id_survey_fk +
                ", uid_score='" + uid_score + '\'' +
                ", score=" + score +
                '}';
    }
}
