package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.domain.entity.intent.ConnectVoucher;

public interface IExternalVoucherRegistry {

    interface SenderCallback {
        void onNotInstalledApp();
    }

    interface Callback {
        void onSuccess();

        void onError();
    }

    void sendVoucher(ConnectVoucher voucher, SenderCallback senderCallback);

    void onResult(int requestCode, int resultCode,
                  Object data, Callback callback);

    void init();

}
