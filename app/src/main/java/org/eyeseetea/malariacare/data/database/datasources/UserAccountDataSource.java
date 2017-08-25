package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.entity.UserAccount;


public class UserAccountDataSource implements IUserRepository {
    @Override
    public UserAccount getLoggedUser() {
        UserDB userDB = UserDB.getLoggedUser();
        UserAccount userAccount = null;
        if (userDB != null) {
            userAccount = new UserAccount(userDB.getName(), userDB.getUid(), false);
            userAccount.setCanAddSurveys(userDB.canAddSurveys());
            userAccount.setPhone(getUserPhone());
            userAccount.setAppVersion(userAccount.getAppVersion());
        }

        return userAccount;
    }

    @Override
    public void saveLoggedUser(UserAccount userAccount) {
        UserDB userDB = new UserDB(userAccount.getUserUid(), userAccount.getUserName());
        userDB.setCanAddSurveys(userAccount.canAddSurveys());
        UserDB.insertLoggedUser(userDB);
    }


    private Phone getUserPhone() {
        Context context = PreferencesState.getInstance().getContext();
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        String phoneIMEI = tMgr.getDeviceId();

        return new Phone(phoneNumber, phoneIMEI);
    }

    private String getAppVersion() {
        Context context = PreferencesState.getInstance().getContext();
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(
                    context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(info.versionCode);
    }

}
