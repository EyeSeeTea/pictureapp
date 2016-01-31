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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;

/**
 * Created by ignac on 17/10/2015.
 */
public class ShowException extends Exception{
        String message;
        Throwable cause;
        public ShowException() {
            super();
        }

        public ShowException(String message, Throwable cause)
        {
            super(message, cause);
            this.cause = cause;
            this.message = message;
        }

        public ShowException(String message, Context context)
        {
            super(message);
            this.message = message;
            showError(message,context);
        }

        public static void showError(int messageId){
            Context context = PreferencesState.getInstance().getContext();
            String message=context.getString(messageId);
            showError(message,context);
        }

        public static void showError(String message,Context context){
            Intent intent = new Intent(context, Dialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", message);
            intent.putExtra("title","");
            context.getApplicationContext().startActivity(intent);
        }


}
