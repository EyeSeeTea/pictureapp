package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.entity.Device;

public class GetDeviceUseCase implements UseCase {
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IDeviceRepository mDeviceRepository;

    public GetDeviceUseCase(
            IMainExecutor mainExecutor,
            IDeviceRepository deviceRepository) {
        mMainExecutor = mainExecutor;
        mDeviceRepository = deviceRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mMainExecutor.run(this);
    }

    @Override
    public void run() {
        Device device = mDeviceRepository.getDevice();
        if(device!=null) {
            mCallback.onSuccess(device);
        }else{
            mCallback.onError();
        }
    }

    public interface Callback {
        void onSuccess(Device device);

        void onError();
    }
}
