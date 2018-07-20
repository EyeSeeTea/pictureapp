package org.eyeseetea.malariacare.data.remote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.element.utils.ElementSDKManager;

import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.domain.entity.intent.ConnectVoucher;

public class ElementController implements IExternalVoucherRegistry, ElementSDKManager.ElementActivityListener, ElementSDKManager.EnrollListener {

    final private String TAG = "ElementController";
    final private String ELEMENT_PACKAGE_NAME = "com.element.palm.portal";
    private Context context;
    private Callback mCallback;

    public ElementController(Context context){
        this.context = context;
    }

    @Override
    public void sendVoucher(ConnectVoucher connectVoucher, SenderCallback senderCallback) {
        if (appInstalled(ELEMENT_PACKAGE_NAME)) {
            ElementSDKManager.enrollNewUser((Activity) context, connectVoucher.getUid(), null);
        }else{
            senderCallback.onNotInstalledApp();
        }
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
        mCallback.onSuccess();
    }

    @Override
    public void onUserFailedToEnroll() {
        Log.d(TAG, "onUserFailedToEnroll");
        mCallback.onError();
    }


    private boolean appInstalled(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }
}
