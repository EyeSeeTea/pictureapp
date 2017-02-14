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

import android.util.Log;

import org.eyeseetea.malariacare.data.sync.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.VisitableFromSDK;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by arrizabalaga on 5/11/15.
 */
public class EventExtended implements VisitableFromSDK {

    public final static String COMPLETION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public final static String AMERICAN_DATE_FORMAT = "yyyy-MM-dd";
    private final static String TAG = ".EventExtended";


    public static final Event.EventStatus STATUS_ACTIVE = Event.EventStatus.ACTIVE;
    public static final Event.EventStatus STATUS_COMPLETED = Event.EventStatus.COMPLETED;
    public static final Event.EventStatus STATUS_SKIPPED = Event.EventStatus.SKIPPED;

    EventFlow event;

    public EventExtended() {
        this.event = new EventFlow();
    }

    public EventExtended(EventFlow event) {
        this.event = event;
    }

    /**
     * Turns a given date into a parseable String according to sdk date format
     */
    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMPLETION_DATE_FORMAT);
        return simpleDateFormat.format(date);
    }

    /**
     * Turns a given date into a parseable String according to sdk date format
     */
    public static String format(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    @Override
    public void accept(IConvertFromSDKVisitor visitor) {
        visitor.visit(this);
    }

    public EventFlow getEvent() {
        return event;
    }

    /**
     * Returns the survey.creationDate associated with this event (created field)
     */
    public Date getCreationDate() {
        if (event == null) {
            return null;
        }

        return event.getCreated().toDate();
    }

    /**
     * Returns the survey.completionDate associated with this event (lastUpdated field)
     */
    public Date getCompletionDate() {
        if (event == null) {
            return null;
        }

        return event.getLastUpdated().toDate();
    }

    /**
     * Returns the survey.eventDate associated with this event (eventDate field)
     */
    public Date getEventDate() {
        if (event == null) {
            return null;
        }

        return event.getEventDate().toDate();
    }

    /**
     * Returns the survey.eventDate associated with this event (dueDate field)
     */
    public Date getScheduledDate() {
        if (event == null) {
            return null;
        }

        return event.getDueDate().toDate();
    }

    private Date parseDate(String dateAsString) {
        if (dateAsString == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMPLETION_DATE_FORMAT);
        try {
            return simpleDateFormat.parse(dateAsString);
        } catch (ParseException e) {
            Log.e(TAG, String.format("Event (%s) cannot parse date %s", event.getUId(),
                    e.getLocalizedMessage()));
            return null;
        }
    }

    public List<DataValueExtended> getDataValues() {
        //// FIXME: 09/11/2016
        return null;
    }

    public String getOrganisationUnitId() {
        return event.getOrgUnit();
    }

    public String getProgramUId() {
        return event.getProgram();
    }

    public String getUid() {
        return event.getUId();
    }

    public Long getLocalId() {
        return event.getId();
    }

    public void delete() {
        event.delete();
    }

    public void setStatus(Event.EventStatus statusCompleted) {
        event.setStatus(statusCompleted);
    }

    //// FIXME: 09/11/2016
    public void setFromServer(boolean value) {
        return;
    }

    public void setOrganisationUnitId(String orgUnitUID) {
        event.setOrgUnit(orgUnitUID);
    }

    public void setProgramId(String uid) {
        event.setProgram(uid);
    }

    public void setProgramStageId(String uid) {
        event.setProgramStage(uid);
    }

    public void save() {
        event.save();
    }

    public void setLastUpdated(DateTime time) {
        event.setLastUpdated(time);
    }

    public void setEventDate(DateTime dateTime) {
        event.setEventDate(dateTime);
    }

    public void setDueDate(DateTime dateTime) {
        event.setDueDate(dateTime);
    }

    public void setLatitude(double latitude) {
        event.setLatitude(latitude);
    }

    public void setLongitude(double longitude) {
        event.setLongitude(longitude);
    }

    public static List<EventExtended> getExtendedList(List<EventFlow> events){
        List <EventExtended> eventExtendeds = new ArrayList<>();
        for(EventFlow pojoFlow:events){
            eventExtendeds.add(new EventExtended(pojoFlow));
        }
        return eventExtendeds;
    }
}
