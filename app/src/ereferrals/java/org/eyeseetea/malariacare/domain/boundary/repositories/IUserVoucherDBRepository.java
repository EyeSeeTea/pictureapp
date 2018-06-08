package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.intent.UserVoucher;

public interface IUserVoucherDBRepository {
    UserVoucher createUserVoucherFromEventUId(String eventUId);
}
