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

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.DashboardAdapterStrategy;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.views.TextCard;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public abstract class AAssessmentAdapter extends ADashboardAdapter implements IDashboardAdapter {

    protected int backIndex = 0;
    protected boolean showNextFacilityName = true;

    private DashboardAdapterStrategy mDashboardAdapterStrategy;

    public AAssessmentAdapter() {
    }

    public AAssessmentAdapter(List<Survey> items, Context context) {
        this.items = items;
        this.context = context;
        this.lInflater = LayoutInflater.from(context);
        this.headerLayout = R.layout.assessment_header;
        this.recordLayout = R.layout.assessment_record;
        this.footerLayout = R.layout.assessment_footer;
        this.title = context.getString(R.string.assessment_title_header);

        mDashboardAdapterStrategy = new DashboardAdapterStrategy(context, this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Survey survey = (Survey) getItem(position);

        View rowView = this.lInflater.inflate(getRecordLayout(), parent, false);

        mDashboardAdapterStrategy.renderSurveySummary(rowView, survey);

        rowView.setBackgroundResource(LayoutUtils.calculateBackgrounds(position));

        return rowView;
    }

    public void showDate(View rowView, int viewId, Date dateValue) {
        TextCard eventDateTextCard = (TextCard) rowView.findViewById(viewId);
        if (dateValue != null) {

            //it show dd/mm/yy in europe, mm/dd/yy in america, etc.
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                    DateFormat.SHORT, Resources.getSystem().getConfiguration().locale);

            eventDateTextCard.setText(formatter.format(dateValue));
        }
    }

    public void showInfo(View rowView, int viewId, String infoValue) {
        TextCard info = (TextCard) rowView.findViewById(viewId);
        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.specific_language_font));
        info.setTypeface(tf);

        info.setText(infoValue);
    }

    public void showRDT(View rowView, int viewId, String RDTValue) {
        TextCard rdt = (TextCard) rowView.findViewById(viewId);

        rdt.setText(RDTValue);
    }

    @Override
    public void notifyDataSetChanged() {
        this.showNextFacilityName = true;
        super.notifyDataSetChanged();
    }
}