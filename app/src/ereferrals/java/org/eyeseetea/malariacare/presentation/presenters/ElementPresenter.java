package org.eyeseetea.malariacare.presentation.presenters;

import android.view.View;

import org.eyeseetea.malariacare.domain.usecase.SendNewUserVoucherToElementUseCase;
import org.eyeseetea.malariacare.domain.usecase.SendUserVoucherToElementUseCase;

public class ElementPresenter {

    View mView;
    String mVoucherUId;
    SendUserVoucherToElementUseCase mSendUserVoucherToElementUseCase;
    SendNewUserVoucherToElementUseCase mSendNewUserVoucherToElementUseCase;

    public ElementPresenter(String voucherUId, SendUserVoucherToElementUseCase sendUserVoucherToElementUseCase,
            SendNewUserVoucherToElementUseCase sendNewUserVoucherToElementUseCase) {
        mVoucherUId = voucherUId;
        mSendUserVoucherToElementUseCase = sendUserVoucherToElementUseCase;
        mSendNewUserVoucherToElementUseCase = sendNewUserVoucherToElementUseCase;
    }

    public void attachView(final View view) {
        mView = view;
        loadElement();
    }

    public void detachView() {
        mView = null;
    }

    private void loadElement() {
        mSendUserVoucherToElementUseCase.execute(mVoucherUId);
    }

    public void createUser() {
        mSendNewUserVoucherToElementUseCase.execute(mVoucherUId);
    }
}