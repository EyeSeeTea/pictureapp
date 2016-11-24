package org.eyeseetea.malariacare.layout;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;

/**
 * Created by idelcano on 01/09/2016.
 */
public class SurveyInfoUtils {

    public static String getRDTSymbol(Context context, Survey survey) {
        String rdtName = survey.getRDTName();
        String rdtResultCode = survey.getResultCode();
        String rdtValue = (rdtName.equals("")) ? context.getResources().getString(
                R.string.unrecognized_option) : rdtName;
        String rdtSymbol = rdtValue;
        if (rdtValue.equals(context.getResources().getString(R.string.rdtPositive))) {
            //The test result is in rdtResultCode
            rdtSymbol = rdtResultCode;
        } else if (rdtValue.equals(context.getResources().getString(R.string.rdtNegative))) {
            //the negative RDT is a not tested
            rdtSymbol = context.getResources().getString(R.string.not_tested);
        }
        return rdtSymbol;
    }


}
