package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

public class InitExternalVoucherRegisterUseCase implements UseCase {

    private IExternalVoucherRegistry mController;

    public InitExternalVoucherRegisterUseCase(
            IExternalVoucherRegistry controller){
                mController = controller;
    }

    public void execute() {
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.init();
    }
}
