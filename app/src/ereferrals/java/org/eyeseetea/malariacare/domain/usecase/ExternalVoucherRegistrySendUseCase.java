package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

public class ExternalVoucherRegistrySendUseCase implements UseCase {

    String mVoucherUId;

    private IExternalVoucherRegistry mController;

    public ExternalVoucherRegistrySendUseCase(
            IExternalVoucherRegistry controller){
                mController = controller;
    }

    public void execute(String voucherUId) {
        mVoucherUId = voucherUId;
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.sendVoucherUId(mVoucherUId);
    }
}
