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

package org.eyeseetea.malariacare.data.sync.importer.models;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.sync.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.VisitableFromSDK;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 5/11/15.
 */
public class DataValueExtended implements VisitableFromSDK {

    private final static String TAG = ".DataValueExtended";
    private final static String REGEXP_FACTOR = ".*\\[([0-9]*)\\]";

    DataValueFlow dataValue;

    public DataValueExtended() {
        dataValue = new DataValueFlow();
    }

    public DataValueExtended(DataValueFlow dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public void accept(IConvertFromSDKVisitor visitor) {
        visitor.visit(this);
    }

    public DataValueFlow getDataValue() {
        return dataValue;
    }

    public Option findOptionByQuestion(Question question) {
        if (question == null) {
            return null;
        }

        Answer answer = question.getAnswer();
        if (answer == null) {
            return null;
        }

        List<Option> options = answer.getOptions();
        List<String> optionCodes = new ArrayList<>();
        for (Option option : options) {
            String optionName = option.getName();
            optionCodes.add(optionName);
            if (optionName == null) {
                continue;
            }

            if (optionName.equals(dataValue.getValue())) {
                return option;
            }
        }

        //Log.w(TAG,String.format("Cannot find option '%s'",dataValue.getValue()));
        return null;
    }

    public String getDataElement() {
        //// FIXME: 28/12/16
        return dataValue.getDataElement();
    }

    public String getValue() {
        return dataValue.getValue();
    }

    public String getEvent() {
        return dataValue.getEvent();
    }

    public void setDataElement(String uid) {
        dataValue.setDataElement(uid);
    }
    public void setLocalEventId(Long id) {
        dataValue.setLocalId(id);
    }

    public void setEvent(EventFlow event) {
        dataValue.setEvent(event.getUId());
    }

    public void setProvidedElsewhere(boolean b) {
        dataValue.setProvidedElsewhere(b);
    }

    public void setStoredBy(String name) {
        dataValue.setStoredBy(name);
    }

    public void setValue(String value) {
        dataValue.setValue(value);
    }

    public void save() {
        dataValue.save();
    }
}
