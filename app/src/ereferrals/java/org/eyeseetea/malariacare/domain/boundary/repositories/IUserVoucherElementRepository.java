package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.intent.UserVoucher;

public interface IUserVoucherElementRepository {
    void sendUserVoucher(UserVoucher userVoucher);
    void createUserVoucher(UserVoucher userVoucher);
}
