package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.network.ServerAPIController;


public class UserAccountRemoteDataSource implements IUserRepository {
    @Override
    public UserAccount getLoggedUser() {
        User user = User.getLoggedUser();
        UserAccount userAccount = null;
        if (user != null) {
            userAccount = new UserAccount(user.getName(), user.getUid(), false);
        }
        return ServerAPIController.getUser(userAccount);
    }

    @Override
    public void saveLoggedUser(UserAccount userAccount) {
    }
}
