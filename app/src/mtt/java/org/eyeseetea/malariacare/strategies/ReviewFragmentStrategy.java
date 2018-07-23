package org.eyeseetea.malariacare.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    public List<String> createBackgroundColorList(
            List<Value> values, Context context) {
        List<java.lang.String> colorsList = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            int color = i % 2 == 0 ? R.color.green_option_2 : R.color.default_background_survey;
            colorsList.add(context.getResources().getString(color));
        }
        return colorsList;
    }
}
