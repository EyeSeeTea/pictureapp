package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.domain.entity.UserAccount;

public interface IUserAccountRepository {

    interface RemoveCurrentUserAccountCallback {
        void onSuccess();

        void onError(Throwable throwable);
    }

    void removeCurrentUserAccount(RemoveCurrentUserAccountCallback callback);
}
