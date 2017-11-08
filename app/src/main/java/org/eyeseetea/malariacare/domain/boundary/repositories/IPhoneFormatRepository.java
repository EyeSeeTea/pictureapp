package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.PhoneFormat;

public interface IPhoneFormatRepository {

    PhoneFormat getUserPhoneFormat();

    interface Callback {
        void onSuccess(PhoneFormat phoneFormat);

        void onError();
    }
}
