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
import org.eyeseetea.malariacare.layout.SurveyInfoUtils;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.views.TextCard;

import java.text.DateFormat;
import java.util.List;

public abstract class AAssessmentAdapter extends ADashboardAdapter implements IDashboardAdapter {

    protected int backIndex = 0;
    protected boolean showNextFacilityName = true;

    public AAssessmentAdapter() { }

    public AAssessmentAdapter(List<Survey> items, Context context) {
        this.items = items;
        this.context = context;
        this.lInflater = LayoutInflater.from(context);
        this.headerLayout = R.layout.assessment_header;
        this.recordLayout = R.layout.assessment_record;
        this.footerLayout = R.layout.assessment_footer;
        this.title = context.getString(R.string.assessment_title_header);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Survey survey = (Survey) getItem(position);

        // Get the row layout
        View rowView = this.lInflater.inflate(getRecordLayout(), parent, false);
        //To ease testing
        rowView.setTag(survey.getId_survey());


        //Event Date
        TextCard eventDate = (TextCard) rowView.findViewById(R.id.completionDate);
        if(survey.getEventDate()!=null){
            //it show dd/mm/yy in europe, mm/dd/yy in america, etc.
            DateFormat formatter=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Resources.getSystem().getConfiguration().locale);

            eventDate.setText(formatter.format(survey.getEventDate()));
        }

        //RDT
        TextCard rdt = (TextCard) rowView.findViewById(R.id.rdt);
        //Since there are three possible values first question (RDT):'Yes','No','Cancel'
        //rdt.setText(survey.isRDT()?"+":"-");

        rdt.setText(SurveyInfoUtils.getRDTSymbol(context, survey));

        //INFO
        TextCard info = (TextCard) rowView.findViewById(R.id.info);
        //Load a font which support Khmer character
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" +  context.getString(R.string.specific_language_font));
        info.setTypeface(tf);

        info.setText(survey.getValuesToString());

        rowView.setBackgroundResource(LayoutUtils.calculateBackgrounds(position));
        return rowView;
    }

    @Override
    public void notifyDataSetChanged(){
        this.showNextFacilityName = true;
        super.notifyDataSetChanged();
    }
}