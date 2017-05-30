package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.layout.utils.LayoutUtils;
import org.eyeseetea.malariacare.views.question.CommonQuestionView;
import org.eyeseetea.malariacare.views.question.IImageQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

public class LabelMultiQuestionView extends CommonQuestionView implements IQuestionView,
        IMultiQuestionView, IImageQuestionView {
    CustomTextView header;
    ImageView imageView;
    CustomTextView helpTextView;

    public LabelMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public void setImage(String path) {
        if (path != null && !path.equals("")) {
            LayoutUtils.makeImageVisible(path, imageView);
        } else {
            adaptLayoutToTextOnly(findViewById(R.id.question_text_container), imageView);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public void setHelpText(String helpText) {
        helpTextView.setText(helpText);
    }

    @Override
    public void setValue(Value value) {
    }

    @Override
    public boolean hasError() {
        return false;
    }

    private void init(Context context) {
        inflate(context, R.layout.multi_question_tab_label_row, this);

        header = (CustomTextView) findViewById(R.id.row_header_text);
        imageView = ((ImageView) findViewById(R.id.question_image_row));
        helpTextView = (CustomTextView) findViewById(R.id.row_help_text);
    }

    private void adaptLayoutToTextOnly(View viewWithText, ImageView rowImageLabelView) {
        //Modify the text weight if the label don't have a image.
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f);
        rowImageLabelView.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        viewWithText.setLayoutParams(params);
    }
}
