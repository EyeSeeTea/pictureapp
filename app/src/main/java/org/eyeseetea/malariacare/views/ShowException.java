package org.eyeseetea.malariacare.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
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
            Intent intent = new Intent(context, Dialog.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("message", message);
            intent.putExtra("title","");
            context.getApplicationContext().startActivity(intent);
        }
}
