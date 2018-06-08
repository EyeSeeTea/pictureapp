package org.eyeseetea.malariacare.domain.entity.intent;

public class UserVoucher {
    private String name;
    private String voucherUId;

    public UserVoucher(String name, String voucherUId) {
        this.name = name;
        this.voucherUId = voucherUId;
    }

    public String getName() {
        return name;
    }

    public String getVoucherUId() {
        return voucherUId;
    }
}
