package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserVoucherDBRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserVoucherElementRepository;
import org.eyeseetea.malariacare.domain.entity.intent.UserVoucher;

public class SendNewUserVoucherToElementUseCase implements UseCase {
    private static final String TAG = "SendUserVoucherToElementUseCase";
    private IUserVoucherDBRepository mUserVoucherRepository;
    private IUserVoucherElementRepository mUserVoucherElementRepository;
    private String mSurveyUId;

    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public SendNewUserVoucherToElementUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IUserVoucherDBRepository userVoucherRepository,
            IUserVoucherElementRepository userVoucherElementRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mUserVoucherRepository = userVoucherRepository;
        mUserVoucherElementRepository = userVoucherElementRepository;
    }
    @Override
    public void run() {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                UserVoucher userVoucher = mUserVoucherRepository.createUserVoucherFromEventUId(mSurveyUId);
                mUserVoucherElementRepository.createUserVoucher(userVoucher);
            }
        });

    }

    public void execute(String surveyUId) {
        mSurveyUId = surveyUId;
        mAsyncExecutor.run(this);
    }
}
