package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import android.content.Context;
import android.widget.ImageView;

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

    @Override
    public void initViews(int totalOptions, ImageView image) {
        Context context = image.getContext();
        int imageSize;
        switch (totalOptions) {
            case 2:
                imageSize = R.dimen.image_option_2_size;
                break;
            case 4:
                imageSize = R.dimen.image_option_4_size;
                break;
            case 6:
                imageSize = R.dimen.image_option_6_size;
                break;
            default:
                imageSize = R.dimen.image_option_2_size;
                break;
        }
        int pixelsSize = context.getResources().getDimensionPixelSize(imageSize);
        image.getLayoutParams().height = pixelsSize;
        image.getLayoutParams().width = pixelsSize;
    }
}
