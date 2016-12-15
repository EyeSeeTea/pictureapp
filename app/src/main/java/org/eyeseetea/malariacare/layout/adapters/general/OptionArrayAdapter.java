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

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.List;

/**
 * Created by adrian on 30/04/15.
 */
public class OptionArrayAdapter extends AddlArrayAdapter<Option> {

    public OptionArrayAdapter(Context context, List<Option> options) {
        super(context, options);
    }

    @Override
    public void drawText(TextCard textCard, Option option) {
        if (textCard.getmScale().equals(getContext().getString(R.string.font_size_system))) {
            textCard.setTextSize(
                    PreferencesState.getInstance().getContext().getResources().getDimension(
                            R.dimen.input_number_edit_text_phone)
                            / PreferencesState.getInstance().getContext().getResources()
                            .getDisplayMetrics().density);
        }
        textCard.setmDimension(getContext().getResources().getString(R.string.font_size_level2));
        textCard.setmFontName(
                getContext().getResources().getString(R.string.specific_language_font));
        textCard.setText(Utils.getInternationalizedString(option.getCode()));
    }

}
