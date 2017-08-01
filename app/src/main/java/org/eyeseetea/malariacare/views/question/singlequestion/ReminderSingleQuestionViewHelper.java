package org.eyeseetea.malariacare.views.question.singlequestion;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;

public class ReminderSingleQuestionViewHelper {

    public static void setImage(View rowView, String path) {
        if (path != null && !path.equals("")) {
            ImageView imageView = (ImageView) rowView.findViewById(R.id.questionImageRow);
            BaseLayoutUtils.putImageInImageViewDensityHigh(path, imageView);
            imageView.setVisibility(View.VISIBLE);
            DynamicTabAdapter.swipeTouchListener.addTouchableView(imageView);
        }
    }

    public static void setWarningText(View rootView, Option option) {
        TextView okText = (TextView) rootView.findViewById(R.id.questionTextRow);
        okText.setText(option.getInternationalizedName());
        DynamicTabAdapter.swipeTouchListener.addTouchableView(okText);
    }

    public static void setWarningValue(View rootView, final Option option,
            final ReminderSingleQuestionView reminderSingleQuestionView) {
        ImageView imageOK = (ImageView) rootView.findViewById(R.id.confirm_yes);
        imageOK.setImageResource(R.drawable.option_button);

        imageOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DynamicTabAdapter.isClicked) {
                    DynamicTabAdapter.isClicked = true;
                    reminderSingleQuestionView.notifyAnswerChanged(option);
                }
            }
        });

        TextView okText = (TextView) rootView.findViewById(R.id.textcard_confirm_yes);
        okText.setText(option.getInternationalizedName());
    }
}
