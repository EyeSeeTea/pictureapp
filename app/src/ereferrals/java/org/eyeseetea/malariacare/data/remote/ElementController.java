package org.eyeseetea.malariacare.data.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.element.utils.ElementSDKManager;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;

public class ElementController implements IExternalVoucherRegistry, ElementSDKManager.ElementActivityListener, ElementSDKManager.EnrollListener {

    final private String TAG = "ElementController";
    private Context context;
    private Callback mCallback;

    public ElementController(Context context){
        this.context = context;
    }

    @Override
    public void sendVoucherUId(String voucherUId) {
        ElementSDKManager.enrollNewUser((Activity) context, voucherUId, null);
    }

    @Override
    public void onResult(int requestCode, int resultCode, Object data, Callback callback) {
        mCallback = callback;
        ElementSDKManager.onActivityResult(requestCode, resultCode, (Intent) data, this);
    }

    @Override
    public void init(){
        ElementSDKManager.initElementSDK(context);
    }

    @Override
    public void onUserEnrolled(String id, boolean isNewUser) {
        Log.d(TAG,  "onUserEnrolled id:"+ id +" new:"+ isNewUser);
        mCallback.onSuccess(id);
    }

    @Override
    public void onUserFailedToEnroll() {
        Log.d(TAG, "onUserFailedToEnroll");
        mCallback.onError();
    }
}
