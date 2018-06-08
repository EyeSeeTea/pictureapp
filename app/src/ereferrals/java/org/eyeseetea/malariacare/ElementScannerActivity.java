package org.eyeseetea.malariacare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.element.sync.ElementSyncState;
import com.element.utils.ElementSDKManager;

import org.eyeseetea.malariacare.data.database.datasources.UserVoucherLocalDataSource;
import org.eyeseetea.malariacare.data.remote.UserVoucherElementDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.usecase.SendNewUserVoucherToElementUseCase;
import org.eyeseetea.malariacare.domain.usecase.SendUserVoucherToElementUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.ElementPresenter;

import java.util.List;

public class ElementScannerActivity extends Activity implements ElementSDKManager.ElementActivityListener, ElementSDKManager.SearchListener, ElementSDKManager.SyncStateListener{



    public static final int ELEMENT_SEARCH_USER_REQUEST = 13372;
    private ElementPresenter mPresenter;
    private String voucherUId;
    private View mView;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.element_activity);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = super.onCreateView(parent, name, context, attrs);
        Intent intent = getIntent();
        voucherUId = intent.getStringExtra(getString(R.string.survey_voucher_key));
        mActivity = this;
        initializePresenter(view, mActivity);
        return view;
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();

        super.onDestroy();
    }

    private void initializePresenter(final View view, final Activity activity) {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        SendUserVoucherToElementUseCase userVoucherUseCase = new SendUserVoucherToElementUseCase(mainExecutor, asyncExecutor,
                new UserVoucherLocalDataSource(getApplicationContext()), new UserVoucherElementDataSource(activity));
        SendNewUserVoucherToElementUseCase newUserVoucherToElementUseCase = new SendNewUserVoucherToElementUseCase(mainExecutor, asyncExecutor,
                new UserVoucherLocalDataSource(getApplicationContext()), new UserVoucherElementDataSource(activity));
        mPresenter = new ElementPresenter(voucherUId, userVoucherUseCase, newUserVoucherToElementUseCase);
        mPresenter.attachView(view);
    }
    /**
     * Handles resolution callbacks.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if(requestCode == ELEMENT_SEARCH_USER_REQUEST) {
            ElementSDKManager.onActivityResult(requestCode, resultCode, data, this);
        }

    }

    @Override
    public void onUsersFound(List<String> list, boolean b) {
        Log.d("Element test ina Users", list.toString() + b);
    }

    @Override
    public void onNoUsersFound() {
        Log.d("Element test ina", "no user found");
        mPresenter.createUser();
    }

    @Override
    public void onIdentifyCanceled() {
        Log.d("Element test ina", "identify cancelled");
    }

    @Override
    public void onSyncStateFetched(ElementSyncState elementSyncState) {
        Log.d("Element test ina onSync", elementSyncState.toString());

    }

    @Override
    public void onSyncStateRequestCancel() {
        Log.d("Element test ina onSync", "cancelled");

    }
}
