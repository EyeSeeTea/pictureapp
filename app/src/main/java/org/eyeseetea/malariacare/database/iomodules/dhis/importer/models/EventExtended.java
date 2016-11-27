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

package org.eyeseetea.malariacare.database.iomodules.dhis.importer.models;

import android.util.Log;

import org.eyeseetea.malariacare.database.iomodules.dhis.importer.IConvertFromSDKVisitor;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.VisitableFromSDK;
import org.hisp.dhis.android.sdk.persistence.models.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by arrizabalaga on 5/11/15.
 */
public class EventExtended implements VisitableFromSDK {

    public final static String COMPLETION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public final static String AMERICAN_DATE_FORMAT = "yyyy-MM-dd";
    public static final int MAX_MONTHS_LOADED = -6;
    private final static String TAG = ".EventExtended";
    Event event;

    public EventExtended() {
    }

    public EventExtended(Event event) {
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

    public Event getEvent() {
        return event;
    }

    /**
     * Returns the survey.creationDate associated with this event (created field)
     */
    public Date getCreationDate() {
        if (event == null) {
            return null;
        }

        return parseDate(event.getCreated());
    }

    /**
     * Returns the survey.completionDate associated with this event (lastUpdated field)
     */
    public Date getCompletionDate() {
        if (event == null) {
            return null;
        }

        return parseDate(event.getLastUpdated());
    }

    /**
     * Returns the survey.eventDate associated with this event (eventDate field)
     */
    public Date getEventDate() {
        if (event == null) {
            return null;
        }

        return parseDate(event.getEventDate());
    }

    /**
     * Returns the survey.eventDate associated with this event (dueDate field)
     */
    public Date getScheduledDate() {
        if (event == null) {
            return null;
        }

        return parseDate(event.getDueDate());
    }

    private Date parseDate(String dateAsString) {
        if (dateAsString == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(COMPLETION_DATE_FORMAT);
        try {
            return simpleDateFormat.parse(dateAsString);
        } catch (ParseException e) {
            Log.e(TAG, String.format("Event (%s) cannot parse date %s", event.getUid(),
                    e.getLocalizedMessage()));
            return null;
        }
    }

    public boolean isTooOld() {
        Date eventDate = getEventDate();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, MAX_MONTHS_LOADED);
        return eventDate.compareTo(calendar.getTime()) < 0;

    }
}
