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

package org.eyeseetea.malariacare.layout.adapters.dashboard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.DashboardAdapterStrategy;
import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.IAssessmentAdapterStrategy;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class AssessmentAdapter extends BaseAdapter implements IDashboardAdapter {

    protected LayoutInflater lInflater;
    protected Context context;
    protected Integer headerLayout;
    protected Integer footerLayout;
    protected Integer recordLayout;
    protected String title;
    List<Survey> items;

    private IAssessmentAdapterStrategy mDashboardAdapterStrategy;

    public AssessmentAdapter(String title, List<Survey> items, Context context) {
        this.items = items;
        this.context = context;
        this.lInflater = LayoutInflater.from(context);
        this.headerLayout = R.layout.assessment_header;
        this.recordLayout = R.layout.assessment_record;
        this.footerLayout = R.layout.assessment_footer;
        this.title = title;

        mDashboardAdapterStrategy = new DashboardAdapterStrategy(context, this);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setItems(List items) {
        this.items = (List<Survey>) items;
    }

    @Override
    public Integer getHeaderLayout() {
        return this.headerLayout;
    }

    @Override
    public Integer getFooterLayout() {
        return footerLayout;
    }

    @Override
    public Integer getRecordLayout() {
        return this.recordLayout;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Context getContext() {
        return this.context;
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void remove(Object item) {
        this.items.remove(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Survey survey = (Survey) getItem(position);

        View rowView = this.lInflater.inflate(getRecordLayout(), parent, false);

        mDashboardAdapterStrategy.renderSurveySummary(rowView, survey);

        LayoutUtils.fixRowViewBackground(rowView, position);


        LayoutUtils.setListRowBackgroundColor(rowView);

        return rowView;
    }

    public void showDate(View rowView, int viewId, Date dateValue) {
        CustomTextView eventDateTextCard = (CustomTextView) rowView.findViewById(viewId);
        if (dateValue != null) {

            //it show dd/mm/yy in europe, mm/dd/yy in america, etc.
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.SHORT, Resources.getSystem().getConfiguration().locale);

            eventDateTextCard.setText(formatter.format(dateValue));
        }
    }

    public void showInfo(View rowView, int viewId, String infoValue) {
        CustomTextView info = (CustomTextView) rowView.findViewById(viewId);
        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.specific_language_font));
        info.setTypeface(tf);

        info.setText(infoValue);
    }

    public void showRDT(View rowView, int viewId, String RDTValue) {
        CustomTextView rdt = (CustomTextView) rowView.findViewById(viewId);

        rdt.setText(RDTValue);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    /**
     * Checks if the given position points to a real survey and open it.
     */
    public void onClick(ListView l, int position, List<Survey> surveys) {
        //Discard clicks on header|footer (which is attended on newSurvey via super)
        if (!isPositionASurvey(l, surveys, position)) {
            return;
        }
        //fixed the position in the list if the adapter have a header.
        int fixedPosition = getFixedPosition(l);
        //Put selected survey in session
        position = position - fixedPosition;
        if(position < 0 || position >= surveys.size()) {
            return;
        }
        Survey malariaSurvey = surveys.get(position);
        Session.setMalariaSurvey(malariaSurvey);
        if (mDashboardAdapterStrategy.hasAllComplementarySurveys(malariaSurvey)) {
            // Go to SurveyActivity
            DashboardActivity.dashboardActivity.openSentSurvey();
        }
    }


    /**
     * Gets the number of displaced positions.
     */
    public int getFixedPosition(ListView l) {
        int fixedPosition = 0;
        if (l.getHeaderViewsCount() >= 1) {
            fixedPosition = l.getHeaderViewsCount();
        }
        return fixedPosition;
    }


    /**
     * Checks if the given position points to a real survey instead of a footer or header of the
     * listview.
     *
     * @return true|false
     */
    public boolean isPositionASurvey(ListView l, List<Survey> surveys, int position) {
        int headerSize = l.getHeaderViewsCount();
        if (headerSize > 0) {
            if (isPositionHeader(position)) {
                return false;
            }
        }
        if (l.getFooterViewsCount() > 0) {
            if (isPositionFooter(surveys, position, headerSize)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given position is the header of the listview instead of a real survey
     *
     * @return true|false
     */
    public boolean isPositionHeader(int position) {
        return position <= 0;
    }

    /**
     * Checks if the given position is the footer of the listview instead of a real survey
     *
     * @return true|false
     */
    public boolean isPositionFooter(List<Survey> surveys, int position, int headerSize) {
        return position >= (surveys.size() + headerSize);
    }
}