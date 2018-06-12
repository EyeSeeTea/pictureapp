package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.IElementController;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;

public class ElementOnActivityResultUseCase implements UseCase {

    int mRequestCode;
    int mResultCode;
    Object mData;

    private IElementController mController;

    public ElementOnActivityResultUseCase(
            IElementController controller){
                mController = controller;
    }

    public void execute(int requestCode, int resultCode, Object data) {
        mRequestCode = requestCode;
        mResultCode = resultCode;
        mData = data;
        AsyncExecutor asyncExecutor = new AsyncExecutor();
        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        mController.onActivityResult(mRequestCode, mResultCode, mData);
    }
}
