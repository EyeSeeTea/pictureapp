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

package org.eyeseetea.malariacare.views;

import android.util.AttributeSet;

/**
 * Created by nacho on 02/06/15.
 */
public interface IEyeSeeView {

    public void init(AttributeSet attrs, int defStyle);

    public void setmFontName(String mFontName);

    public String getmFontName();

    public void setmDimension(String mDimension);

    public String getmDimension();

    public void setmScale(String mScale);

    public String getmScale();
}
