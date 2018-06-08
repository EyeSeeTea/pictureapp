package org.eyeseetea.malariacare.data.remote;

import android.app.Activity;

import com.element.utils.ElementSDKManager;

import org.eyeseetea.malariacare.domain.boundary.repositories.IUserVoucherElementRepository;
import org.eyeseetea.malariacare.domain.entity.intent.UserVoucher;

import java.util.ArrayList;
import java.util.HashMap;

public class UserVoucherElementDataSource implements IUserVoucherElementRepository {

    private Activity mActivity;

    public UserVoucherElementDataSource(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void sendUserVoucher(UserVoucher userVoucher) {
        ArrayList<String> users = new ArrayList<>();
        users.add(userVoucher.getVoucherUId());
        ElementSDKManager.identifyUser(mActivity, users);
    }
    @Override
    public void createUserVoucher(UserVoucher userVoucher) {
        ArrayList<String> users = new ArrayList<>();
        users.add(userVoucher.getVoucherUId());
        HashMap<String, String> userHashMap = new HashMap<>();
        userHashMap.put(userVoucher.getName(), userVoucher.getVoucherUId());
        ElementSDKManager.enrollNewUser(mActivity, userVoucher.getName(), userHashMap);
    }
}
