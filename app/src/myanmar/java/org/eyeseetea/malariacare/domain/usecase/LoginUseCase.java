package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IRepositoryCallback;
import org.eyeseetea.malariacare.domain.boundary.IUserAccountRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class LoginUseCase extends ALoginUseCase {
    private IUserAccountRepository mUserAccountRepository;

    public LoginUseCase(IUserAccountRepository userAccountRepository) {
        mUserAccountRepository = userAccountRepository;
    }

    @Override
    public void execute(Credentials credentials, final Callback callback) {

        mUserAccountRepository.login(credentials,
                new IRepositoryCallback<UserAccount>() {
                    @Override
                    public void onSuccess(UserAccount userAccount) {
                        createDummyDataInDB();
                        callback.onLoginSuccess();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onLoginError(throwable.getMessage());
                    }
                });
    }

    //TODO: jsanchez - Fix with new architecture
    private void createDummyDataInDB() {
/*        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(context.getAssets());
                PullController.convertOUinOptions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public boolean isLogoutNeeded(Credentials credentials) {
        return false;
    }
}
