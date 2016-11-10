package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.database.model.OrgUnit;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;

import java.io.IOException;
import java.util.List;

public class LoginUseCase extends ALoginUseCase{
    @Override
    protected void executeCustomActions(Credentials credentials, Context context) {

    }
}
