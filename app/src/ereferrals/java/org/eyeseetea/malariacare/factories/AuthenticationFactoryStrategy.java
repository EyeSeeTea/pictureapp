package org.eyeseetea.malariacare.factories;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.net.ConnectivityManager;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.usecase.ForgotPasswordUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;

public class AuthenticationFactoryStrategy extends AAuthenticationFactory {

    @Override
    public LoginUseCase getLoginUseCase(Context context) {
        IConnectivityManager connectivityManager = new ConnectivityManager(context);
        IAuthenticationManager authenticationManager = getAuthenticationManager(context);
        ICredentialsRepository credentialsLocalDataSource = getCredentialsRepository();
        IOrganisationUnitRepository organisationDataSource =
                new OrganisationUnitRepository();
        IInvalidLoginAttemptsRepository
                iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        LoginUseCase loginUseCase = new LoginUseCase(connectivityManager,
                authenticationManager, mainExecutor,
                asyncExecutor, organisationDataSource, credentialsLocalDataSource,
                iInvalidLoginAttemptsRepository);

        return loginUseCase;
    }

    public ForgotPasswordUseCase getForgotPasswordUseCase(Context context) {
        IAuthenticationManager authenticationManager = super.getAuthenticationManager(context);

        ForgotPasswordUseCase forgotPasswordUseCase = new ForgotPasswordUseCase(mainExecutor,
                asyncExecutor, authenticationManager);

        return forgotPasswordUseCase;
    }

    @NonNull
    public ICredentialsRepository getCredentialsRepository() {
        return new CredentialsLocalDataSource();
    }

}
