package org.eyeseetea.malariacare.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.presentation.presenters.SoftLoginPresenter;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class SoftLoginDialogFragment extends DialogFragment implements SoftLoginPresenter.View {

    private CustomEditText userNameEditText;
    private CustomEditText passwordEditText;
    private Button loginButton;
    private LinearLayout softLoginContainer;
    private ProgressBar progressBar;
    private Button logoutButton;
    private Button advancedOptions;

    private SoftLoginPresenter presenter;

    public static SoftLoginDialogFragment newInstance() {
        SoftLoginDialogFragment fragment = new SoftLoginDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_soft_login, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);

        initializeViews(view);
        initializePresenter();

        String EXIT = "exit";
    }

    private void initializeViews(View view) {
        initializeBasicViews(view);

        loginButton = view.findViewById(R.id.button_log_in);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login(passwordEditText.getText().toString());
            }
        });

        logoutButton = view.findViewById(R.id.button_log_out);

        advancedOptions = view.findViewById(R.id.advanced_options);

        advancedOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.advancedOptions();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login(passwordEditText.getText().toString());
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                presenter.logout();
            }
        });
    }

    private void initializeBasicViews(View view) {
        softLoginContainer = view.findViewById(R.id.soft_login_container);
        progressBar = view.findViewById(R.id.soft_login_progress_bar);
        userNameEditText = view.findViewById(R.id.edittext_username);
        userNameEditText.setEnabled(false);
        passwordEditText = view.findViewById(R.id.edittext_password);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                presenter.onPinChanged(s.toString());
            }
        });
    }

    private void initializePresenter() {
        AuthenticationFactoryStrategy factoryStrategy = new AuthenticationFactoryStrategy();

        presenter = factoryStrategy.getSoftLoginPresenter(getActivity());
        presenter.attachView(this);
    }

    @Override
    public void showUsername(String username) {
        userNameEditText.setText(username);
    }

    @Override
    public void showProgress() {
        passwordEditText.setEnabled(false);
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void softLoginSuccess() {
        dismiss();
    }

    @Override
    public void showInvalidPinError() {
        showError(R.string.login_invalid_credentials);
    }

    @Override
    public void showNetworkError() {
        showError(R.string.network_error);
    }

    @Override
    public void disableLoginAction() {
        loginButton.setEnabled(false);
    }

    @Override
    public void enableLoginAction() {
        loginButton.setEnabled(true);
    }

    @Override
    public void showAdvancedOptions() {
        logoutButton.setVisibility(View.VISIBLE);
        advancedOptions.setText(R.string.simple_options);
    }

    @Override
    public void hideAdvancedOptions() {
        logoutButton.setVisibility(View.GONE);
        advancedOptions.setText(R.string.advanced_options);
    }

    @Override
    public void navigateToLogin() {
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(loginIntent);
    }

    @Override
    public void showLogoutError() {
        showError(R.string.login_unexpected_error);
    }

    @Override
    public void showInvalidAuthFromExternalApp() {
        showError(R.string.different_user_error);
    }

    @Override
    public void showServerNotAvailable(String message) {
        showError(message);
    }

    public void showError(int message) {
        Toast.makeText(this.getActivity(), translate(message),
                Toast.LENGTH_LONG).show();
    }

    public void showError(String message) {
        Toast.makeText(this.getActivity(), message,
                Toast.LENGTH_LONG).show();
    }

    public String translate(@StringRes int id) {
        return Utils.getInternationalizedString(id, this.getActivity());
    }
}
