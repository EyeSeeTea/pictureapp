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

package org.eyeseetea.malariacare;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.okhttp.HttpUrl;
import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.strategies.LoginActivityStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Login Screen.
 * It shows only when the user has an open session.
 */
public class LoginActivity extends org.hisp.dhis.android.sdk.ui.activities.LoginActivity {

    public static final String PULL_REQUIRED = "PULL_REQUIRED";
    public static final String DEFAULT_USER = "";
    public static final String DEFAULT_PASSWORD = "";
    private static final String TAG = ".LoginActivity";
    public LoginUseCase mLoginUseCase = new LoginUseCase(this);
    public LoginActivityStrategy mLoginActivityStrategy = new LoginActivityStrategy(this);
    EditText serverText;
    EditText usernameEditText;
    EditText passwordEditText;
    private String serverUrl;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLoginActivityStrategy.onCreate();

        initDataDownloadPeriodDropdown();

        //Populate server with the current value
        serverText = (EditText) findViewById(R.id.server_url);
        serverText.setText(ServerAPIController.getServerUrl());

        //Username, Password blanks to force real login
        usernameEditText = (EditText) findViewById(R.id.username);
        usernameEditText.setText(DEFAULT_USER);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setText(DEFAULT_PASSWORD);
    }

    private void initDataDownloadPeriodDropdown() {
        if (!BuildConfig.loginDataDownloadPeriod) {
            return;
        }
        //Add left text for the spinner "title"
        findViewById(R.id.date_spinner_container).setVisibility(View.VISIBLE);
        TextView textView = (TextView) findViewById(R.id.data_text_view);
        textView.setText(R.string.download);

        //add options
        ArrayList<String> dataLimitOptions = new ArrayList<>();
        dataLimitOptions.add(getString(R.string.no_data));
        dataLimitOptions.add(getString(R.string.last_6_days));
        dataLimitOptions.add(getString(R.string.last_6_weeks));
        dataLimitOptions.add(getString(R.string.last_6_months));

        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dataLimitOptions);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //add spinner
        Spinner spinner = (Spinner) findViewById(R.id.data_spinner);
        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                PreferencesState.getInstance().setDataLimitedByDate(
                        spinnerArrayAdapter.getItem(pos).toString());
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //select the selected option or default no data option
        String dateLimit = PreferencesState.getInstance().getDataLimitedByDate();
        if (dateLimit.equals("")) {
            spinner.setSelection(spinnerArrayAdapter.getPosition(getString(R.string.no_data)));
        } else {
            spinner.setSelection(spinnerArrayAdapter.getPosition(dateLimit));
        }
    }

    @Override
    public void login(String serverUrl, String username, String password) {
        //This method is overriden to capture credentials data
        this.serverUrl = serverUrl;
        this.username = username;
        this.password = password;


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.eula_accepted), false)) {
            askEula(R.string.settings_menu_eula, R.raw.eula, LoginActivity.this);
        } else {
            loginToDhis(serverUrl, username, password);
        }
    }


    /**
     * Shows an alert dialog asking for acceptance of the EULA terms. If ok calls login function,
     * do
     * nothing otherwise
     */
    public void askEula(int titleId, int rawId, final Context context) {
        InputStream message = context.getResources().openRawResource(rawId);
        String stringMessage = Utils.convertFromInputStreamToString(message).toString();
        final SpannableString linkedMessage = new SpannableString(Html.fromHtml(stringMessage));
        Linkify.addLinks(linkedMessage, Linkify.EMAIL_ADDRESSES | Linkify.WEB_URLS);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(titleId))
                .setMessage(linkedMessage)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rememberEulaAccepted(context);
                        loginToDhis(serverUrl, username, password);
                    }
                })
                .setNegativeButton(android.R.string.no, null).create();

        dialog.show();

        ((TextView) dialog.findViewById(android.R.id.message)).setMovementMethod(
                LinkMovementMethod.getInstance());
    }

    /**
     * Save a preference to remember that EULA was already accepted
     */
    public void rememberEulaAccepted(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.eula_accepted), true);
        editor.commit();
    }

    /**
     * User SDK function to login
     */
    public void loginToDhis(String serverUrl, String username, String password) {
        //Delegate real login attempt to parent in sdk
        super.login(serverUrl, username, password);
    }

    /**
     * This logout is called from the success user autentication, and try to login in the server
     * with the correct userdata.
     */
    @Subscribe
    public void onLogoutFinished(UiEvent uiEvent) {
        //No event or not a logout event -> done
        if (uiEvent == null || !uiEvent.getEventType().equals(UiEvent.UiEventType.USER_LOG_OUT)) {
            return;
        }
        HttpUrl serverUri = HttpUrl.parse(serverUrl);
        DhisService.logInUser(serverUri, ServerAPIController.getSDKCredentials());
    }

    @Subscribe
    public void onLoginFinished(NetworkJob.NetworkJobResult<ResourceType> result) {
        if (result != null && result.getResourceType().equals(ResourceType.USERS)) {
            if (result.getResponseHolder().getApiException() == null) {
                Credentials credentials = new Credentials(serverUrl, username, password);
                mLoginUseCase.execute(credentials);
                //The first login is only to authenticate the user, and is need logout from the
                // sdk and login with the correct user/password.
                if (mLoginUseCase.isLogoutNeeded(credentials)) {
                    username = DEFAULT_USER;
                    password = DEFAULT_PASSWORD;
                    //The first login (user authentication) calls this
                    DhisService.logOutUser(this);
                } else {
                    mLoginActivityStrategy.finishAndGo();
                }
            } else {
                onLoginFail(result.getResponseHolder().getApiException());
            }
        }
    }


    @Override
    public void onBackPressed() {
        mLoginActivityStrategy.onBackPressed();
    }

}



