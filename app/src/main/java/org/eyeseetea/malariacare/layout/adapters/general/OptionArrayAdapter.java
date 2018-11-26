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

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class OptionArrayAdapter extends AddlArrayAdapter<OptionDB> {

    public OptionArrayAdapter(Context context, List<OptionDB> optionDBs) {
        super(context, optionDBs);
    }

    public OptionArrayAdapter(Context context, List<OptionDB> optionDBs, int layout) {
        super(context, layout, optionDBs);
    }

    @Override
    public void drawText(CustomTextView customTextView, OptionDB optionDB) {
        customTextView.setText(Utils.getInternationalizedString(optionDB.getName()));
    }

}
