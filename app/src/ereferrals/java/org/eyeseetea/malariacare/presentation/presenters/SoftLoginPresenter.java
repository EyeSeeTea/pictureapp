package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;

public class SoftLoginPresenter {
    private View view;
    private GetUserUserAccountUseCase getUserUserAccountUseCase;

    public SoftLoginPresenter(GetUserUserAccountUseCase getUserUserAccountUseCase) {
        this.getUserUserAccountUseCase = getUserUserAccountUseCase;
    }

    public void attachView(View view) {
        this.view = view;

        loadCurrentUser();
    }

    public void detachView() {
        view = null;
    }

    private void loadCurrentUser() {
        getUserUserAccountUseCase.execute(new GetUserUserAccountUseCase.Callback() {
            @Override
            public void onGetUserAccount(UserAccount userAccount) {
                if (view != null) {
                    view.showUsername(userAccount.getUserName());
                }
            }
        });
    }

    public interface View {
        void showUsername(String username);
    }
}
