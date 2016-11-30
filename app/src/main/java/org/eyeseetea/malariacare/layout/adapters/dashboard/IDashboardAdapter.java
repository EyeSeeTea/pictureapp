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

import java.util.List;

public interface IDashboardAdapter {

    Context getContext();

    void setContext(Context context);

    Integer getHeaderLayout();

    Integer getFooterLayout();

    Integer getRecordLayout();

    String getTitle();

    void setTitle(String title);

    void setItems(List items);

    Object getItem(int position);

    void remove(Object item);

    void notifyDataSetChanged();

}
