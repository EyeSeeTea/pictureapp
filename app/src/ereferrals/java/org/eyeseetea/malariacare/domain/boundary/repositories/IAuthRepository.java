package org.eyeseetea.malariacare.domain.boundary.repositories;


import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public interface IAuthRepository {

    Auth getAuth();

    void saveAuth(Auth auth);

    void clearAuth();
}
