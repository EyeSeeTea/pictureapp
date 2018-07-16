package org.eyeseetea.malariacare.strategies;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.data.remote.ElementController;
import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.domain.usecase.InitExternalVoucherRegisterUseCase;

public class EyeSeeTeaApplicationStrategy extends AEyeSeeTeaApplicationStrategy {
    private EyeSeeTeaApplication mEyeSeeTeaApplication;

    public EyeSeeTeaApplicationStrategy(
            EyeSeeTeaApplication eyeSeeTeaApplication) {
        mEyeSeeTeaApplication = eyeSeeTeaApplication;
    }

    @Override
    public void onCreate() {
        if (BuildConfig.elementSDK) {
            IExternalVoucherRegistry elementController = new ElementController(
                    mEyeSeeTeaApplication);
            InitExternalVoucherRegisterUseCase initExternalVoucherRegisterUseCase =
                    new InitExternalVoucherRegisterUseCase(elementController);
            initExternalVoucherRegisterUseCase.execute();
        }
    }
}
