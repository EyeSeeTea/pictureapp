package org.eyeseetea.malariacare.views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.factories.SyncFactoryStrategy;
import org.eyeseetea.malariacare.presentation.presenters.PullPresenter;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class PullDialogFragment extends DialogFragment implements PullPresenter.View {

    private ProgressBar progressBar;
    private CustomTextView progressText;

    private PullPresenter presenter;

    public static PullDialogFragment newInstance() {
        PullDialogFragment fragment = new PullDialogFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pull, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);

        initializeViews(view);
        initializePresenter();
    }

    private void initializeViews(View view) {
        progressBar = view.findViewById(R.id.pull_dialog_progress_bar);
        progressText = view.findViewById(R.id.pull_dialog_progress_text);
    }

    private void initializePresenter() {
        SyncFactoryStrategy syncFactoryStrategy = new SyncFactoryStrategy();

        presenter = syncFactoryStrategy.getPullPresenter(getActivity());
        presenter.attachView(this);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        progressText.setTextTranslation(R.string.pull_dialog_progress_generic_message);
    }

    @Override
    public void pullSuccess() {
        getDialog().dismiss();
    }

    @Override
    public void showPullError() {
        getDialog().dismiss();
        showError(R.string.dialog_pull_error);
    }

    @Override
    public void showNetworkError() {
        getDialog().dismiss();
        showError(R.string.network_error);
    }

    @Override
    public void showPullConversionError() {
        getDialog().dismiss();
        showError(R.string.network_error);
    }

    @Override
    public void showWarningError(String warning) {
        getDialog().dismiss();
        showError(
                translate(R.string.warning_message)
                        + warning);
    }

    public void showError(@StringRes int message) {
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
