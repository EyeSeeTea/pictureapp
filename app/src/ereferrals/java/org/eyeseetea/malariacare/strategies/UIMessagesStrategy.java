package org.eyeseetea.malariacare.strategies;

import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

/**
 * Created by ina on 20/12/2016.
 */

public class UIMessagesStrategy extends AUIMessagesStrategy {

    public void showCompulsoryUnansweredToast() {
        Toast.makeText(PreferencesState.getInstance().getContext(),
                PreferencesState.getInstance().getContext().getString(
                        R.string.provider_redeemEntry_msg_warning),
                Toast.LENGTH_LONG).show();
    }
}
