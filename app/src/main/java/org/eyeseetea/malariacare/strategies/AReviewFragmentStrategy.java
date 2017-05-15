package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import android.widget.TableRow;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.List;

public abstract class AReviewFragmentStrategy {

    public abstract TableRow createViewRow(TableRow rowView, Value value);

    public static boolean shouldShowReviewScreen() {
        return getMalariaSurvey().isRDT() || BuildConfig.patientTestedByDefault;
    }

    public List<org.eyeseetea.malariacare.data.database.model.Value> orderValues(
            List<org.eyeseetea.malariacare.data.database.model.Value> values) {
        return values;
    }
}
