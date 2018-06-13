package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;
import org.eyeseetea.malariacare.domain.entity.intent.ConnectVoucher;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

import java.util.HashMap;

public class SendToExternalAppPaperVoucherUseCase implements UseCase {

    private ConnectVoucher voucher;
    private IExternalVoucherRegistry mController;
    private IExternalVoucherRegistry.SenderCallback mSenderCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public SendToExternalAppPaperVoucherUseCase( IMainExecutor mainExecutor, IAsyncExecutor asyncExecutor,
            IExternalVoucherRegistry controller, IExternalVoucherRegistry.SenderCallback senderCallback){
                mController = controller;
                mSenderCallback = senderCallback;
                mMainExecutor = mainExecutor;
                mAsyncExecutor = asyncExecutor;
    }

    public void execute(String voucherUId) {
        voucher = new ConnectVoucher(new Auth(), new HashMap<String, String>(), voucherUId);
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.sendVoucher(voucher, new IExternalVoucherRegistry.SenderCallback() {
            @Override
            public void onNotInstalledApp() {
                notifyAppIsNotInstalled();
            }
        });
    }

    private void notifyAppIsNotInstalled() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mSenderCallback.onNotInstalledApp();
            }
        });
    }
}
