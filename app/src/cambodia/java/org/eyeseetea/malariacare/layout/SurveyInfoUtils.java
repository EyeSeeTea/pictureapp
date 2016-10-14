package org.eyeseetea.malariacare.layout;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;

/**
 * Created by idelcano on 01/09/2016.
 */
public class SurveyInfoUtils {

    public static String getRDTSymbol(Context context, Survey survey) {
        String rdtValueFromDB = survey.getRDTName();
        String rdtValue = (rdtValueFromDB.equals("")) ? context.getResources().getString(R.string.unrecognized_option) : rdtValueFromDB;
        String rdtSymbol = rdtValue;
        if(rdtValue.equals(context.getResources().getString(R.string.rdtPositive))){
            rdtSymbol = context.getResources().getString(R.string.symbolPlus);
        }else if(rdtValue.equals(context.getResources().getString(R.string.rdtNegative))){
            rdtSymbol = context.getResources().getString(R.string.symbolMinus);
        }else if(rdtValue.equals(context.getResources().getString(R.string.rdtNotTested))){
            rdtSymbol = context.getResources().getString(R.string.symbolCross);
        }
        return rdtSymbol;
    }
}
