package org.eyeseetea.malariacare.domain.boundary;

public interface IElementController {

    void sendVoucher(String voucherUId);

    void onActivityResult(int requestCode, int resultCode,
                          Object data);
}
