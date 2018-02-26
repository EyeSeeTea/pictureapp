package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.widget.ImageView;

import org.eyeseetea.malariacare.R;

public abstract class AImageOptionViewStrategy {


    public int getViewForColumns(int columnsCount) {
        return R.layout.dynamic_image_question_option;
    }

    public void initViews(int totalOptions, ImageView image) {

    }
}
