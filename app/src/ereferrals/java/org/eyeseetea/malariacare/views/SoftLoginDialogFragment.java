package org.eyeseetea.malariacare.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    }

    private void initializeViews(View view) {
        softLoginContainer = view.findViewById(R.id.soft_login_container);
        progressBar = view.findViewById(R.id.soft_login_progress_bar);
        userNameEditText = view.findViewById(R.id.edittext_username);
        userNameEditText.setEnabled(false);
        passwordEditText = view.findViewById(R.id.edittext_password);
        loginButton = view.findViewById(R.id.button_log_in);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.login(passwordEditText.getText().toString());
            }
        });

        // Show soft keyboard automatically and request focus to field
        passwordEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
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
    public void loginSuccess() {
        getDialog().dismiss();
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
    public void launchPull() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PullDialogFragment pullDialogFragment = PullDialogFragment.newInstance();
        pullDialogFragment.show(fm, "pull");
    }


    public void showError(int message) {
        Toast.makeText(this.getActivity(), translate(message),
                Toast.LENGTH_LONG).show();
    }

    public String translate(@StringRes int id) {
        return Utils.getInternationalizedString(id, this.getActivity());
    }
}
