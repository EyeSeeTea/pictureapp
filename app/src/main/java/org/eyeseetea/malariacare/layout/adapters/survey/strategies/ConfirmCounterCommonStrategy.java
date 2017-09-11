package org.eyeseetea.malariacare.layout.adapters.survey.strategies;

import android.view.View;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

import utils.ProgressUtils;

public class ConfirmCounterCommonStrategy {
    public static boolean isClicked;
    private DynamicTabAdapter mDynamicTabAdapter;

    public ConfirmCounterCommonStrategy(DynamicTabAdapter dynamicTabAdapter) {
        mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void showStandardConfirmCounter(final View view, final Option selectedOption,
            final Question question,
            Question questionCounter) {
        //Change question x confirm message
        View rootView = view.getRootView();
        final CustomTextView questionView = (CustomTextView) rootView.findViewById(R.id.question);
        questionView.setText(questionCounter.getInternationalizedForm_name());
        ProgressUtils.setProgressBarText(rootView, "");
        //cancel
        ImageView noView = (ImageView) rootView.findViewById(R.id.confirm_no);
        noView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave current question as it was
                removeConfirmCounter(v);
                mDynamicTabAdapter.notifyDataSetChanged();
                isClicked = false;
            }
        });

        //confirm
        ImageView yesView = (ImageView) rootView.findViewById(R.id.confirm_yes);
        yesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDynamicTabAdapter.navigationController.increaseCounterRepetitions(selectedOption);
                removeConfirmCounter(v);
                mDynamicTabAdapter.saveOptionValue(view, selectedOption, question, true);
            }
        });

        //Show confirm on full screen
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);

        //Show question image in counter alert
        if (questionCounter.getPath() != null && !questionCounter.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            BaseLayoutUtils.putImageInImageView(questionCounter.getInternationalizedPath(),
                    imageView);
            imageView.setVisibility(View.VISIBLE);
            DynamicTabAdapter.swipeTouchListener.addTouchableView(imageView);
        }

        //Question "header" is in the first option in Options.csv
        List<Option> questionOptions = questionCounter.getAnswer().getOptions();
        if (questionOptions.get(0) != null) {
            CustomTextView textCard = (CustomTextView) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(questionOptions.get(0).getInternationalizedName());
            DynamicTabAdapter.swipeTouchListener.addTouchableView(textCard);
        }
        //Question "confirm button" is in the second option in Options.csv
        if (questionOptions.get(1) != null) {
            CustomTextView confirmTextCard = (CustomTextView) rootView.findViewById(
                    R.id.textcard_confirm_yes);
            confirmTextCard.setText(questionOptions.get(1).getInternationalizedName());
        }
        //Question "no confirm button" is in the third option in Options.csv
        if (questionOptions.get(2) != null) {
            CustomTextView noConfirmTextCard = (CustomTextView) rootView.findViewById(
                    R.id.textcard_confirm_no);
            noConfirmTextCard.setText(questionOptions.get(2).getInternationalizedName());
        }
    }

    private void removeConfirmCounter(View view) {
        view.getRootView().findViewById(R.id.dynamic_tab_options_table).setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.confirm_table).setVisibility(View.GONE);
    }
}
