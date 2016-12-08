package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.events.UiEvent;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    private static final int MENU_ITEM_LOGOUT = 99;
    private static final int MENU_ITEM_LOGOUT_ORDER = 106;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onCreate() {
        //Register into sdk bug for listening to logout events
        Dhis2Application.bus.register(this);
    }

    @Override
    public void onStop() {
        try {
            //Unregister from bus before leaving
            Dhis2Application.bus.unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, MENU_ITEM_LOGOUT_ORDER,
                mBaseActivity.getResources().getString(R.string.settings_menu_logout_title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_LOGOUT:
                new AlertDialog.Builder(mBaseActivity)
                        .setTitle(mBaseActivity.getString(R.string.settings_menu_logout_title))
                        .setMessage(mBaseActivity.getString(R.string.dashboard_menu_logout_message))
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        DhisService.logOutUser(mBaseActivity);
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

    @Subscribe
    public void onLogoutFinished(UiEvent uiEvent) {
        //No event or not a logout event -> done
        if (uiEvent == null || !uiEvent.getEventType().equals(UiEvent.UiEventType.USER_LOG_OUT)) {
            return;
        }

        LogoutUseCase logoutUseCase = new LogoutUseCase(mBaseActivity);
        logoutUseCase.execute();

        mBaseActivity.finishAndGo(LoginActivity.class);
    }

}
