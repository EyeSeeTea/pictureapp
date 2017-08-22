package org.eyeseetea.malariacare.fragments.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy {

    @Override
    public void initFooter(final View footer) {
        IUserRepository userReposit = new UserAccountDataSource();
        GetUserUserAccountUseCase getUserUserAccountUseCase =
                new GetUserUserAccountUseCase(userReposit);
        getUserUserAccountUseCase.execute(new GetUserUserAccountUseCase.Callback() {
            @Override
            public void onGetUserAccount(UserAccount userAccount) {
                int visibility = userAccount.canAddSurveys() ? View.VISIBLE : View.GONE;
                footer.setVisibility(visibility);
            }
        });
    }
}
