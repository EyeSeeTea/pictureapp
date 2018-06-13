package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.data.remote.ElementController;

public interface IExternalVoucherRegistry {

    interface Callback {
        void onSuccess();

        void onError();
    }

    void sendVoucherUId(String voucherUId);

    void onResult(int requestCode, int resultCode,
                  Object data, ElementController.Callback callback);

    void init();

}
