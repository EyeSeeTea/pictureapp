package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IElementController;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

public class ElementSentVoucherUseCase implements UseCase {

    String mVoucherUId;

    private IElementController mController;

    public ElementSentVoucherUseCase(
            IElementController controller){
                mController = controller;
    }

    public void execute(String voucherUId) {
        mVoucherUId = voucherUId;
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.sendVoucher(mVoucherUId);
    }
}
