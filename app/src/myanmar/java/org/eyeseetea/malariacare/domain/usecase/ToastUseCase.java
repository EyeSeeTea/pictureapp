package org.eyeseetea.malariacare.domain.usecase;

import android.widget.Toast;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

/**
 * Created by idelcano on 14/12/2016.
 */

public class ToastUseCase {

    public static void showCompulsoryUnansweredToast() {
        Toast.makeText(PreferencesState.getInstance().getContext(),
                PreferencesState.getInstance().getContext().getString(
                        R.string.error_compulsory_question_unanswered),
                Toast.LENGTH_LONG).show();
    }
}
