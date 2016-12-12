package org.eyeseetea.malariacare.domain.usecase;

import android.app.Activity;

import org.eyeseetea.malariacare.LoginActivity;

/**
 * Created by idelcano on 12/12/2016.
 */

public class InitUseCase extends AInitUseCase {
    public InitUseCase(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public void finishAndGo() {
        finishAndGo(LoginActivity.class);
    }
}
