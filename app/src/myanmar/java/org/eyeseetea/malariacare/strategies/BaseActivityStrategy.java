package org.eyeseetea.malariacare.strategies;

import static android.R.attr.settingsActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;

public class BaseActivityStrategy extends ABaseActivityStrategy {

    private static final int MENU_ITEM_LOGOUT=99;
    private static final int MENU_ITEM_LOGOUT_ORDER=106;

    public BaseActivityStrategy(BaseActivity baseActivity) {
        super(baseActivity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT,MENU_ITEM_LOGOUT_ORDER, mBaseActivity.getResources().getString(R.string.settings_menu_logout_title));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case MENU_ITEM_LOGOUT:
                LogoutUseCase logoutUseCase = new LogoutUseCase(mBaseActivity);
                logoutUseCase.execute();
                mBaseActivity.finishAndGo(LoginActivity.class);
                break;
            default:
                return false;
        }
        return true;
    }

}
