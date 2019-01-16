package org.eyeseetea.malariacare.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.presentation.presenters.SoftLoginPresenter;
import org.eyeseetea.sdk.presentation.views.CustomEditText;

public class SoftLoginDialogFragment extends DialogFragment implements SoftLoginPresenter.View {

    private CustomEditText userNameEditText;
    private CustomEditText passwordEditText;
    private Button loginButton;

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
        userNameEditText = view.findViewById(R.id.edittext_username);
        userNameEditText.setEnabled(false);
        passwordEditText = view.findViewById(R.id.edittext_password);
        loginButton = view.findViewById(R.id.button_log_in);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        // Show soft keyboard automatically and request focus to field
        passwordEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void initializePresenter() {
        AuthenticationFactoryStrategy factoryStrategy = new AuthenticationFactoryStrategy();

        presenter = factoryStrategy.getSoftLoginPresenter();
        presenter.attachView(this);
    }

    @Override
    public void showUsername(String username) {
        userNameEditText.setText(username);
    }
}
