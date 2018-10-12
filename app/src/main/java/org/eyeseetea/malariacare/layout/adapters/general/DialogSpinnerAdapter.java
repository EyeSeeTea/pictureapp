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

package org.eyeseetea.malariacare.layout.adapters.general;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class DialogSpinnerAdapter extends OptionArrayAdapter  {

    private LayoutInflater lInflater;
    private  Context context;
    private OptionDB selectedOption;
    private int layout;

    public DialogSpinnerAdapter(Context context, List<OptionDB> optionDBs, int layout, OptionDB selectedOption) {
        super(context, optionDBs, layout);
        this.context = context;
        this.layout = layout;
        this.lInflater = LayoutInflater.from(context);
        this.selectedOption = selectedOption;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.layout, parent, false);
        }

        //Set text item
        drawText((CustomTextView) convertView.findViewById(android.R.id.text1), getItem(position));

        int notSelected = R.drawable.spinner_divider;
        int selected = ContextCompat.getColor(context, android.R.color.secondary_text_dark);
        OptionDB optionDB = getItem(position);
        if(optionDB!=null && optionDB.equals(selectedOption)){
            convertView.setBackgroundColor(selected);
        }
        else {
            convertView.setBackgroundResource(notSelected);
        }
        // Return the completed view to render on screen
        return convertView;
    }
}