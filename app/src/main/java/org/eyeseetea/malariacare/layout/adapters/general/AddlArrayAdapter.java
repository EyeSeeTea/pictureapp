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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public abstract class AddlArrayAdapter<T> extends ArrayAdapter<T> {
    private Integer layout;
    private Integer dropdownItemSize;

    public AddlArrayAdapter(Context context, List<T> objects) {
        super(context, R.layout.simple_spinner_item, objects);
        this.layout = R.layout.simple_spinner_item;
    }

    public AddlArrayAdapter(Context context, Integer layout, List<T> objects) {
        super(context, layout, objects);
        this.layout = layout;
    }


    public abstract void drawText(CustomTextView textCard, T object);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.layout, parent, false);
        }

        //Set text item
        drawText((CustomTextView) convertView.findViewById(android.R.id.text1), getItem(position));

        // Return the completed view to render on screen
        return convertView;
    }

    // When the ddl is popped up
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(this.layout, parent, false);
        }

        //Set text item
        drawText((CustomTextView) convertView.findViewById(android.R.id.text1), getItem(position));
        if(dropdownItemSize!=null) {
            ((CustomTextView) convertView).setTextSize(dropdownItemSize);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public void setDropDownItemSize(Integer dropdownItemSize){
        this.dropdownItemSize=dropdownItemSize;
    }
}