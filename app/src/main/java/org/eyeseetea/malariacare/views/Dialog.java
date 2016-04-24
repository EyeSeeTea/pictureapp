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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import org.eyeseetea.malariacare.R;

/**
 * Created by ignac on 17/10/2015.
 */
public class Dialog extends Activity{
    private AlertDialog.Builder alertDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        String title = intent.getStringExtra("title");
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(R.string.accept,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                goBack();
            }
        });
        alertDialog.show();
}
    public void goBack(){
        ((Activity) this).finish();
    }
    public boolean deleteDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            //@Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_NEUTRAL:
                        break;
                }
            }
        };
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setMessage("Are you sure to delete?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener)
                .show();
        return true;
    }
}
