package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.BaseActivity;

public class CopyRightDialogUseCase {
    public static void showCopyRight(BaseActivity baseActivity, int app_copyright, int copyright) {
        BaseActivity.showAlertWithHtmlMessage(baseActivity, app_copyright, copyright);
    }
}
