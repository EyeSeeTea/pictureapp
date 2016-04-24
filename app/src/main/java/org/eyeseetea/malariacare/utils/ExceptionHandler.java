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

package org.eyeseetea.malariacare.utils;

import android.app.Activity;
import android.util.Log;

/**
 * Created by nacho on 02/05/15.
 */
public class ExceptionHandler implements Thread.UncaughtExceptionHandler{
    Activity context;

    public ExceptionHandler(Activity context){
        this.context = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // TODO: Here we will put the code to do before closing an Activity that has crashed
        Log.e("ExceptionHandler", "--> ERROR: " + ex.getMessage());
        ex.printStackTrace();
    }
}
