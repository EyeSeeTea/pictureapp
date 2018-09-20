package org.eyeseetea.malariacare.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.IAuthenticationDataSource;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AuthenticationLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.net.ConnectivityManager;
import org.eyeseetea.malariacare.data.remote.AuthenticationWSDataSource;
import org.eyeseetea.malariacare.data.sync.exporter.eReferralsAPIClient;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.usecase.ForgotPasswordUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;

public class AuthenticationFactoryStrategy extends AAuthenticationFactory {

    @Override
    public LoginUseCase getLoginUseCase(Context context) {
        IConnectivityManager connectivityManager = new ConnectivityManager(context);
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);
        ICredentialsRepository credentialsLocalDataSource = getCredentialsRepository();

        IInvalidLoginAttemptsRepository iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();

        LoginUseCase loginUseCase = new LoginUseCase(connectivityManager,
                authenticationManager, mainExecutor, asyncExecutor, credentialsLocalDataSource,
                iInvalidLoginAttemptsRepository);

        return loginUseCase;
    }

    @Override
    public IAuthenticationManager getAuthenticationManager(Context context) {
        IAuthenticationDataSource userAccountLocalDataSource =
                new AuthenticationLocalDataSource(context);

        eReferralsAPIClient eReferralsAPIClient = new eReferralsAPIClient(
                PreferencesEReferral.getWSURL());

        IAuthenticationDataSource userAccountRemoteDataSource =
                new AuthenticationWSDataSource(eReferralsAPIClient);

        return new AuthenticationManager(context,
                userAccountLocalDataSource, userAccountRemoteDataSource);
    }

    public ForgotPasswordUseCase getForgotPasswordUseCase(Context context) {
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);

        ForgotPasswordUseCase forgotPasswordUseCase = new ForgotPasswordUseCase(mainExecutor,
                asyncExecutor, authenticationManager);

        return forgotPasswordUseCase;
    }

    @NonNull
    public ICredentialsRepository getCredentialsRepository() {
        return new CredentialsLocalDataSource();
    }
}
