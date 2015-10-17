package org.eyeseetea.malariacare.views;

import android.app.AlertDialog;
import android.content.Context;

import org.eyeseetea.malariacare.R;

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

        public ShowException(String message,Context context)
        {
            super(message);
            this.message = message;
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("");
            alert.setMessage(message);
            alert.setPositiveButton(R.string.accept,null);
            alert.show();
        }
}
