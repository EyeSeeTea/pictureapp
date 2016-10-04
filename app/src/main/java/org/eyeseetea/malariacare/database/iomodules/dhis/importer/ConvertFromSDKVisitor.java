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

package org.eyeseetea.malariacare.database.iomodules.dhis.importer;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.UserAccountExtended;
import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.TabGroup;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.models.UserAccount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertFromSDKVisitor implements IConvertFromSDKVisitor {

    private final static String TAG=".ConvertFromSDKVisitor";


    TabGroup tabgroup;
    Map<String,Object> appMapObjects;
    List<Survey> surveys;
    List<Value> values;



    public ConvertFromSDKVisitor(){
        Program firstProgram = Program.getFirstProgram();
        tabgroup = firstProgram.getTabGroups().get(0);
        appMapObjects = new HashMap();
        surveys = new ArrayList<>();
        values = new ArrayList<>();
    }

    public Map<String, Object> getAppMapObjects() {
        return appMapObjects;
    }

    public void setAppMapObjects(Map<String, Object> appMapObjects) {
        this.appMapObjects = appMapObjects;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    /**
     * Turns a sdk organisationUnit into an app OrgUnit
     *
     * @param sdkOrganisationUnitExtended
     */
    @Override
    public void visit(OrganisationUnitExtended sdkOrganisationUnitExtended) {
        //Create and save OrgUnitLevel
        OrganisationUnit organisationUnit=sdkOrganisationUnitExtended.getOrgUnit();
        org.eyeseetea.malariacare.database.model.OrgUnitLevel orgUnitLevel = new org.eyeseetea.malariacare.database.model.OrgUnitLevel();
        if(!appMapObjects.containsKey(String.valueOf(organisationUnit.getLevel()))) {
            orgUnitLevel.setName(PreferencesState.getInstance().getContext().getResources().getString(R.string.create_info_zone));
            orgUnitLevel.save();
            appMapObjects.put(String.valueOf(organisationUnit.getLevel()), orgUnitLevel);
        }
        //create the orgUnit
        org.eyeseetea.malariacare.database.model.OrgUnit appOrgUnit= new org.eyeseetea.malariacare.database.model.OrgUnit();
        //Set name
        appOrgUnit.setName(organisationUnit.getLabel());
        //Set uid
        appOrgUnit.setUid(organisationUnit.getId());
        //Set orgUnitLevel
        appOrgUnit.setOrgUnitLevel((org.eyeseetea.malariacare.database.model.OrgUnitLevel) appMapObjects.get(String.valueOf(organisationUnit.getLevel())));
        //Set the parent
        //At this moment, the parent is a UID of a not pulled Org_unit , without the full org_unit the OrgUnit.orgUnit(parent) is null.
        String parent_id=null;
        parent_id = organisationUnit.getParent();
        if(parent_id!=null && !parent_id.equals("")) {
            appOrgUnit.setOrgUnit((org.eyeseetea.malariacare.database.model.OrgUnit) appMapObjects.get(String.valueOf(parent_id)));
        }
        else
            appOrgUnit.setOrgUnit((OrgUnit)null);
        appOrgUnit.save();
        //Annotate built orgunit
        appMapObjects.put(organisationUnit.getId(), appOrgUnit);
    }

    /**
     * Turns a sdk userAccount into a User
     * @param sdkUserAccountExtended
     */
    @Override
    public void visit(UserAccountExtended sdkUserAccountExtended) {
        UserAccount userAccount=sdkUserAccountExtended.getUserAccount();
        User appUser = new User();
        appUser.setUid(userAccount.getUId());
        appUser.setName(userAccount.getName());
        appUser.save();
    }

    /**
     * Turns an event into a sent survey
     * @param sdkEventExtended
     */
    @Override
    public void visit(EventExtended sdkEventExtended) {
        Event event=sdkEventExtended.getEvent();
        OrgUnit orgUnit =(OrgUnit)appMapObjects.get(event.getOrganisationUnitId());

        Survey survey=new Survey();
        //Any survey that comes from the pull has been sent
        survey.setStatus(Constants.SURVEY_SENT);
        //Set dates
        survey.setCreationDate(sdkEventExtended.getCreationDate());
        survey.setCompletionDate(sdkEventExtended.getCompletionDate());
        survey.setEventDate(sdkEventExtended.getEventDate());
        survey.setScheduledDate(sdkEventExtended.getScheduledDate());
        //Set fks
        survey.setOrgUnit(orgUnit);
        survey.setTabGroup(tabgroup);
        surveys.add(survey);

        //Annotate object in map
        appMapObjects.put(event.getUid(), survey);
    }

    @Override
    public void visit(DataValueExtended sdkDataValueExtended) {
        DataValue dataValue=sdkDataValueExtended.getDataValue();
        Survey survey=(Survey)appMapObjects.get(dataValue.getEvent());
        String questionUID=dataValue.getDataElement();

        //Data value is a value from compositeScore -> ignore
        if(appMapObjects.get(questionUID) instanceof CompositeScore){
            return;
        }

        //Phone metadata -> ignore
        if(PushClient.PHONEMETADA_UID.equals(questionUID)){
            return;
        }

        //Datavalue is a value from a question
        Question question=Question.findByUID(questionUID);

        Value value=new Value();
        value.setQuestion(question);
        value.setSurvey(survey);

        org.eyeseetea.malariacare.database.model.Option option=sdkDataValueExtended.findOptionByQuestion(question);
        value.setOption(option);
        //No option -> text question (straight value)
        if(option==null){
            value.setValue(dataValue.getValue());
        }else{
        //Option -> extract value from code
            value.setValue(sdkDataValueExtended.getDataValue().getValue());
        }
        values.add(value);
    }

}
