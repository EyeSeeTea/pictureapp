package org.eyeseetea.malariacare.strategies;


import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
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

    public void onBadCredentials() {
        loginActivity.hideProgressBar();
        loginActivity.showError(loginActivity.getString(R.string.login_invalid_credentials));
    }

    public void onStart() {
    }

    public void disableLogin() {

    }

    public void onTextChange() {
        loginActivity.getLoginButton().setEnabled(
                !(loginActivity.getServerText().getText().toString().isEmpty()) &&
                        !(loginActivity.getUsernameEditText().getText().toString().isEmpty()) &&
                        !(loginActivity.getPasswordEditText().getText().toString().isEmpty()));
    }

    public abstract void initLoginUseCase(IAuthenticationManager authenticationManager);

    public void checkCredentials(Credentials credentials, Callback callback) {
        callback.onSuccessDoLogin();
    }

    public interface Callback {
        void onSuccess();

        void onSuccessDoLogin();

        void onError();
    }


}
