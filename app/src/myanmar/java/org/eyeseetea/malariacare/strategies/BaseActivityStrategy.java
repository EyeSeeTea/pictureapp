package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.data.sdk.SdkController;
import org.eyeseetea.malariacare.data.sdk.SdkLoginController;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    private static final int MENU_ITEM_LOGOUT = 99;
    private static final int MENU_ITEM_LOGOUT_ORDER = 106;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onCreate() {
        //Register into sdk bug for listening to logout events
        SdkController.register(this);
    }

    @Override
    public void onStop() {
        try {
            //Unregister from bus before leaving
            SdkController.unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, MENU_ITEM_LOGOUT_ORDER,
                mBaseActivity.getResources().getString(R.string.app_logout));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_LOGOUT:
                new AlertDialog.Builder(mBaseActivity)
                        .setTitle(mBaseActivity.getString(R.string.app_logout))
                        .setMessage(mBaseActivity.getString(R.string.dashboard_menu_logout_message))
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        SdkLoginController.logOutUser(mBaseActivity);
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create().show();
                break;
            default:
                return false;
        }
        return true;
    }

    //// FIXME: 28/12/16
    //@Subscribe
    public void onLogoutFinished() {

        LogoutUseCase logoutUseCase = new LogoutUseCase(mBaseActivity);
        logoutUseCase.execute();

        mBaseActivity.finishAndGo(LoginActivity.class);
    }

}
