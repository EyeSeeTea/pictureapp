package org.eyeseetea.malariacare.services.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.usecase.PushMockUseCase;
import org.eyeseetea.malariacare.domain.usecase.PushUseCase;

public class PushServiceStrategy extends APushServiceStrategy{
    public static final String TAG = ".PushServiceStrategy";

    public PushServiceStrategy(Context context) {
        super(context);
    }

    @Override
    public void push(final Callback callback) {
        if (Session.getCredentials().isDemoCredentials())
            executePushMock(callback);
        else
            executePush(callback);
    }

    private void executePushMock(final Callback callback) {
        PushMockUseCase pushMockUseCase = new PushMockUseCase();

        pushMockUseCase.execute(new PushMockUseCase.Callback() {
            @Override
            public void onPushFinished() {
                callback.onPushFinished();
            }
        });
    }

    private void executePush(final Callback callback) {
        PushUseCase pushUseCase = new PushUseCase(mContext);

        pushUseCase.execute(new PushUseCase.Callback() {
            @Override
            public void onPushFinished() {
                callback.onPushFinished();
            }

            @Override
            public void onPushError(String message) {
                callback.onPushError(message);
            }
        });
    }
}
