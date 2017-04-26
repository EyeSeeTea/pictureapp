package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import android.widget.TableRow;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.database.model.Value;

public abstract class AReviewFragmentStrategy {

    public abstract TableRow createViewRow(TableRow rowView, Value value);

    public static boolean shouldShowReviewScreen() {
        return getMalariaSurvey().isRDT() || BuildConfig.patientTestedByDefault;
    }
}
