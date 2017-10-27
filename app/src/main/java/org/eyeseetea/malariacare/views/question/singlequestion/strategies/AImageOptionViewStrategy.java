package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import org.eyeseetea.malariacare.R;

public abstract class AImageOptionViewStrategy {


    public int getViewForColumns(int columnsCount) {
        return R.layout.dynamic_image_question_option;
    }
}
