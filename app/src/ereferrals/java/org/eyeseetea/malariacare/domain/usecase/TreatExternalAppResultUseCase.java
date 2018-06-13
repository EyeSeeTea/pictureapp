package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

public class TreatExternalAppResultUseCase implements UseCase {

    int mRequestCode;
    int mResultCode;
    Object mData;
    IExternalVoucherRegistry.Callback mCallback;

    private IExternalVoucherRegistry mController;

    public TreatExternalAppResultUseCase(
            IExternalVoucherRegistry controller, IExternalVoucherRegistry.Callback callback){
        mController = controller;
        mCallback = callback;
    }

    public void execute(int requestCode, int resultCode, Object data) {
        mRequestCode = requestCode;
        mResultCode = resultCode;
        mData = data;
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.onResult(mRequestCode, mResultCode, mData, mCallback);
    }
}
