/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.sync.importer;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.CompositeScoreDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.UserAccountExtended;
import org.eyeseetea.malariacare.data.sync.importer.strategies.ConvertFromSDKVisitorStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertFromSDKVisitor implements IConvertFromSDKVisitor {

    private final static String TAG = ".ConvertFromSDKVisitor";
    private Context mContext;

    Map<String, Object> appMapObjects;
    List<SurveyDB> mSurveyDBs;
    List<ValueDB> mValueDBs;
    List<OrgUnitDB> mOrgUnitDBs;

    private ConvertFromSDKVisitorStrategy mConvertFromSDKVisitorStrategy;


    public ConvertFromSDKVisitor(Context context) {
        mContext = context;
        appMapObjects = new HashMap();
        mSurveyDBs = new ArrayList<>();
        mValueDBs = new ArrayList<>();
        mOrgUnitDBs = new ArrayList<>();

        mConvertFromSDKVisitorStrategy = new ConvertFromSDKVisitorStrategy(context);
    }

    public Map<String, Object> getAppMapObjects() {
        return appMapObjects;
    }

    public void setAppMapObjects(Map<String, Object> appMapObjects) {
        this.appMapObjects = appMapObjects;
    }

    public List<SurveyDB> getSurveyDBs() {
        return mSurveyDBs;
    }

    public List<OrgUnitDB> getOrgUnitDBs() {
        return mOrgUnitDBs;
    }

    public void setSurveyDBs(List<SurveyDB> surveyDBs) {
        this.mSurveyDBs = surveyDBs;
    }

    public List<ValueDB> getValueDBs() {
        return mValueDBs;
    }

    public void setValueDBs(List<ValueDB> valueDBs) {
        this.mValueDBs = valueDBs;
    }

    /**
     * Turns a sdk organisationUnit into an app OrgUnitDB
     */
    @Override
    public void visit(OrganisationUnitExtended sdkOrganisationUnitExtended) {
        OrgUnitDB appOrgUnitDB = new OrgUnitDB();

        appOrgUnitDB.setName(sdkOrganisationUnitExtended.getLabel());
        appOrgUnitDB.setUid(sdkOrganisationUnitExtended.getId());

        appOrgUnitDB.save();

        mOrgUnitDBs.add(appOrgUnitDB);

        appMapObjects.put(sdkOrganisationUnitExtended.getId(), appOrgUnitDB);
    }

    /**
     * Turns a sdk userAccount into a User
     */
    @Override
    public void visit(UserAccountExtended sdkUserAccountExtended) {
        UserDB appUserDB = new UserDB();
        appUserDB.setUid(sdkUserAccountExtended.getUid());
        appUserDB.setName(sdkUserAccountExtended.getName());
        appUserDB.setLastUpdated(null);
        appUserDB.save();
    }

    /**
     * Turns an event into a sent survey
     */
    @Override
    public void visit(EventExtended sdkEventExtended) {
        OrgUnitDB orgUnitDB = (OrgUnitDB) appMapObjects.get(sdkEventExtended.getOrganisationUnitId());
        ProgramDB programDB = ProgramDB.getProgram(sdkEventExtended.getProgramUId());

        SurveyDB surveyDB = new SurveyDB();

        //Any survey that comes from the pull has been sent
        surveyDB.setStatus(Constants.SURVEY_SENT);

        //Set dates
        surveyDB.setCreationDate(sdkEventExtended.getCreationDate());
        surveyDB.setCompletionDate(sdkEventExtended.getEventDate());
        surveyDB.setEventDate(sdkEventExtended.getEventDate());
        surveyDB.setScheduledDate(sdkEventExtended.getScheduledDate());

        //Set fks
        surveyDB.setOrgUnit(orgUnitDB);
        surveyDB.setProgram(programDB);
        surveyDB.setEventUid(sdkEventExtended.getUid());

        mConvertFromSDKVisitorStrategy.visit(sdkEventExtended, surveyDB);

        mSurveyDBs.add(surveyDB);

        //Annotate object in map
        appMapObjects.put(sdkEventExtended.getUid(), surveyDB);
    }


    @Override
    public void visit(DataValueExtended sdkDataValueExtended) {
        SurveyDB surveyDB = (SurveyDB) appMapObjects.get(sdkDataValueExtended.getEvent());
        String questionUID = sdkDataValueExtended.getDataElement();

        //Data valueDB is a valueDB from compositeScore -> ignore
        if (appMapObjects.get(questionUID) instanceof CompositeScoreDB) {
            return;
        }

        //Phone metadata -> ignore
        if (PreferencesState.getInstance().getContext().getString(R.string.control_data_element_phone_metadata).equals(questionUID)) {
            return;
        }

        //Datavalue is a valueDB from a questionDB
        QuestionDB questionDB = QuestionDB.findByUID(questionUID);

        if (questionDB == null) {
            Log.e(TAG, "Question not found with dataelement uid " + questionUID);
        }

        ValueDB valueDB = new ValueDB();
        valueDB.setQuestionDB(questionDB);
        valueDB.setSurveyDB(surveyDB);

        OptionDB optionDB =
                sdkDataValueExtended.findOptionByQuestion(questionDB);
        valueDB.setOptionDB(optionDB);
        //No optionDB -> text questionDB (straight valueDB)
        if (optionDB == null) {
            valueDB.setValue(sdkDataValueExtended.getValue());
        } else {
            //OptionDB -> extract valueDB from code
            valueDB.setValue(sdkDataValueExtended.getDataValue().getValue());
        }
        mValueDBs.add(valueDB);
    }
    @Override
    public void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
        ConvertFromSDKVisitorStrategy.visit(categoryOptionGroupExtended);
    }

    public void setOrgUnitDBs(List<OrgUnitDB> allOrgUnitsInDBDB) {
        mOrgUnitDBs = allOrgUnitsInDBDB;
    }
}
