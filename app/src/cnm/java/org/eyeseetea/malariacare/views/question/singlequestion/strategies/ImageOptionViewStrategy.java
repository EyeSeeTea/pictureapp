package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import org.eyeseetea.malariacare.R;

public class ImageOptionViewStrategy extends AImageOptionViewStrategy {

    @Override
    public int getViewForColumns(int columnsCount) {
        if(columnsCount>1) {
            return super.getViewForColumns(columnsCount);
        }else{
            return R.layout.dynamic_image_question_option_single_column;
        }
    }
}
