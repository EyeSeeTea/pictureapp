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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.strategies.LoginActivityStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.dialog.AnnouncementMessageDialog;
import org.hisp.dhis.client.sdk.ui.views.AbsTextWatcher;

import java.io.InputStream;
import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import fr.castorflex.android.circularprogressbar.CircularProgressDrawable;

/**
 * Login Screen.
 * It shows only when the user has an open session.
 */
public class LoginActivity extends Activity {

    public static final String PULL_REQUIRED = "PULL_REQUIRED";
    public static final String DEFAULT_USER = "";
    public static final String DEFAULT_PASSWORD = "";
    private static final String TAG = ".LoginActivity";
    public IAuthenticationManager mAuthenticationManager = new AuthenticationManager(this);
    public LoginUseCase mLoginUseCase = new LoginUseCase(mAuthenticationManager);
    public LoginActivityStrategy mLoginActivityStrategy = new LoginActivityStrategy(this);
    EditText serverText;
    EditText usernameEditText;
    EditText passwordEditText;
    private Button loginButton;


    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_login);
        PreferencesState.getInstance().onCreateActivityPreferences(getResources(), getTheme());
        AsyncInit asyncPopulateDB = new AsyncInit(this);
        asyncPopulateDB.execute((Void) null);
    }

    private void initDataDownloadPeriodDropdown() {
        if (!BuildConfig.loginDataDownloadPeriod) {
            return;
        }

        ViewGroup loginViewsContainer = (ViewGroup) findViewById(
                R.id.login_dynamic_views_container);

        getLayoutInflater().inflate(R.layout.login_spinner, loginViewsContainer,
                true);

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

    protected void onLoginButtonClicked(Editable server, Editable username, Editable password) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(getString(R.string.eula_accepted), false)) {
            askEula(R.string.app_EULA, R.raw.eula, LoginActivity.this);
        } else {
            login(server.toString(), username.toString(), password.toString());
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
                        login(serverText.getText().toString(),
                                usernameEditText.getText().toString(),
                                passwordEditText.getText().toString());
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

    public void login(String serverUrl, String username, String password) {
        final Credentials credentials = new Credentials(serverUrl, username, password);
        showProgressBar();

        mLoginUseCase.execute(credentials, new ALoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                mLoginActivityStrategy.onLoginSuccess(credentials);
            }

            @Override
            public void onServerURLNotValid() {
                hideProgressBar();
                serverText.setError(getString(R.string.login_invalid_server_url));
                showError(getString(R.string.login_invalid_server_url));
            }

            @Override
            public void onInvalidCredentials() {
                hideProgressBar();
                showError(getString(R.string.login_invalid_credentials));
            }

            @Override
            public void onNetworkError() {
                mLoginActivityStrategy.onLoginNetworkError(credentials);
            }

            @Override
            public void onConfigJsonNotPresent() {
                hideProgressBar();
                showError(getString(R.string.login_error_json));
            }
        });
    }

    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mLoginActivityStrategy.onOptionsItemSelected(item);

        return super.onOptionsItemSelected(item);
    }
    public void showError(int message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        mLoginActivityStrategy.onBackPressed();
    }

    private void init() {
        FieldTextWatcher watcher = new FieldTextWatcher();
        initDataDownloadPeriodDropdown();
        //Populate server with the current value
        serverText = (EditText) findViewById(R.id.edittext_server_url);
        serverText.setText(ServerAPIController.getServerUrl());
        serverText.addTextChangedListener(watcher);

        //Username, Password blanks to force real login
        usernameEditText = (EditText) findViewById(R.id.edittext_username);
        usernameEditText.setText(DEFAULT_USER);
        usernameEditText.addTextChangedListener(watcher);
        passwordEditText = (EditText) findViewById(R.id.edittext_password);
        passwordEditText.setText(DEFAULT_PASSWORD);
        passwordEditText.addTextChangedListener(watcher);
        findViewById(R.id.button_log_out).setVisibility(View.GONE);
        loginButton = (Button) findViewById(R.id.button_log_in);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginButtonClicked(serverText.getText(), usernameEditText.getText(),
                        passwordEditText.getText());
            }
        });
        bar = (CircularProgressBar) findViewById(
                org.hisp.dhis.client.sdk.ui.R.id.progress_bar_circular);
        float progressBarStrokeWidth = getResources()
                .getDimensionPixelSize(
                        org.hisp.dhis.client.sdk.ui.R.dimen.progressbar_stroke_width);
        bar.setIndeterminateDrawable(new CircularProgressDrawable.Builder(this)
                .color(ContextCompat.getColor(this,
                        org.hisp.dhis.client.sdk.ui.R.color.color_primary_default))
                .style(CircularProgressDrawable.STYLE_ROUNDED)
                .strokeWidth(progressBarStrokeWidth)
                .rotationSpeed(1f)
                .sweepSpeed(1f)
                .build());
        mLoginActivityStrategy.initViews();
    }

    public class AsyncInit extends AsyncTask<Void, Void, Exception> {
        Activity activity;

        AsyncInit(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            //// FIXME: 30/12/16  Fix mising progressbar
            //bar = (ProgressBar) activity.findViewById(R.id.progress_bar_circular);
            bar = (ProgressBar) activity.findViewById(R.id.progress_bar_circular);
            bar.setVisibility(View.VISIBLE);
            activity.findViewById(R.id.layout_login_views).setVisibility(View.GONE);
        }

        @Override
        protected Exception doInBackground(Void... params) {
            //TODO jsanchez, Why is called from AsyncTask?, It's not very correct and force
            //run explicitly in main thread accions over views in LoginActivityStrategy
            mLoginActivityStrategy.onCreate();
            return null;
        }

        @Override
        protected void onPostExecute(final Exception exception) {
            //Error
            bar.setVisibility(View.GONE);
            activity.findViewById(R.id.layout_login_views).setVisibility(View.VISIBLE);

            init();
        }
    }

    public void showProgressBar() {
        bar.setVisibility(View.VISIBLE);
        findViewById(R.id.layout_login_views).setVisibility(View.GONE);
    }

    public void hideProgressBar() {
        bar.setVisibility(View.GONE);
        findViewById(R.id.layout_login_views).setVisibility(View.VISIBLE);
    }


    public class AsyncPullAnnouncement extends AsyncTask<LoginActivity, Void, Void> {
        //userCloseChecker is never saved, Only for check if the date is closed.
        LoginActivity loginActivity;
        boolean isUserClosed = false;

        @Override
        protected Void doInBackground(LoginActivity... params) {
            loginActivity = params[0];
            if (Session.getUser() != null) {
                isUserClosed = ServerAPIController.isUserClosed(Session.getUser().getUid());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgressBar();
            if (isUserClosed) {
                Log.d(TAG, "user closed");
                AnnouncementMessageDialog.closeUser(R.string.admin_announcement,
                        PreferencesState.getInstance().getContext().getString(R.string.user_close),
                        LoginActivity.this);
            } else {
                mLoginActivityStrategy.finishAndGo();
            }
        }
    }

    public void checkAnnouncement() {
        PreferencesState.getInstance().setUserAccept(false);
        AsyncPullAnnouncement asyncPullAnnouncement = new AsyncPullAnnouncement();
        asyncPullAnnouncement.execute(LoginActivity.this);
    }

    public void enableLogin(boolean enable) {
        loginButton.setEnabled(enable);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoginActivityStrategy.onStart();
    }

    private void onTextChanged() {
        mLoginActivityStrategy.onTextChange();
    }

    public EditText getServerText() {
        return serverText;
    }

    public EditText getUsernameEditText() {
        return usernameEditText;
    }

    public EditText getPasswordEditText() {
        return passwordEditText;
    }

    public Button getLoginButton() {
        return loginButton;
    }

    private class FieldTextWatcher extends AbsTextWatcher {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            LoginActivity.this.onTextChanged();
        }
    }
}



