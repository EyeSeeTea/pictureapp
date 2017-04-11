package org.eyeseetea.malariacare.strategies;


import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Credentials;

public abstract class ALoginActivityStrategy {
    protected LoginActivity loginActivity;

    public ALoginActivityStrategy(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public abstract void onBackPressed();

    public abstract void finishAndGo();

    public abstract void onCreate();

    public abstract void initViews();

    public abstract void onLoginSuccess(Credentials credentials);

    public void onLoginNetworkError(Credentials credentials) {
        loginActivity.hideProgressBar();
        loginActivity.showError(loginActivity.getString(R.string.network_error));
    }

    public void onStart() {
    }

    public boolean canEnableLoginButtonOnTextChange() {
        return true;
    }
}
