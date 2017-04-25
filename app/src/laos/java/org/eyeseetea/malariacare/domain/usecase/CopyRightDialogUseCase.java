package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.BaseActivity;

public class CopyRightDialogUseCase {
    public static void showCopyRight(BaseActivity baseActivity, int app_copyright, int copyright) {
        BaseActivity.showAlertWithMessage(baseActivity, app_copyright, copyright);
    }
}
