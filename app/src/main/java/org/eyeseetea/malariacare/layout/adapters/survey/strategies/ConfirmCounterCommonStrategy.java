package org.eyeseetea.malariacare.layout.adapters.survey.strategies;

import android.view.View;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
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

    public void showStandardConfirmCounter(final View view, final OptionDB selectedOptionDB,
            final QuestionDB questionDB,
            QuestionDB questionDBCounter) {
        //Change questionDB x confirm message
        View rootView = view.getRootView();
        final CustomTextView questionView = (CustomTextView) rootView.findViewById(R.id.question);
        questionView.setText(questionDBCounter.getInternationalizedForm_name());
        ProgressUtils.setProgressBarText(rootView, "");
        //cancel
        ImageView noView = (ImageView) rootView.findViewById(R.id.confirm_no);
        noView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leave current questionDB as it was
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
                mDynamicTabAdapter.navigationController.increaseCounterRepetitions(selectedOptionDB);
                removeConfirmCounter(v);
                mDynamicTabAdapter.saveOptionValue(view, selectedOptionDB, questionDB, true);
            }
        });

        //Show confirm on full screen
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);

        //Show questionDB image in counter alert
        if (questionDBCounter.getPath() != null && !questionDBCounter.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            BaseLayoutUtils.putImageInImageView(questionDBCounter.getInternationalizedPath(),
                    imageView);
            imageView.setVisibility(View.VISIBLE);
            DynamicTabAdapter.swipeTouchListener.addTouchableView(imageView);
        }

        //QuestionDB "header" is in the first option in Options.csv
        List<OptionDB> questionOptionDBs = questionDBCounter.getAnswerDB().getOptionDBs();
        if (questionOptionDBs.get(0) != null) {
            CustomTextView textCard = (CustomTextView) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(questionOptionDBs.get(0).getInternationalizedName());
            DynamicTabAdapter.swipeTouchListener.addTouchableView(textCard);
        }
        //QuestionDB "confirm button" is in the second option in Options.csv
        if (questionOptionDBs.get(1) != null) {
            CustomTextView confirmTextCard = (CustomTextView) rootView.findViewById(
                    R.id.textcard_confirm_yes);
            confirmTextCard.setText(questionOptionDBs.get(1).getInternationalizedName());
        }
        //QuestionDB "no confirm button" is in the third option in Options.csv
        if (questionOptionDBs.get(2) != null) {
            CustomTextView noConfirmTextCard = (CustomTextView) rootView.findViewById(
                    R.id.textcard_confirm_no);
            noConfirmTextCard.setText(questionOptionDBs.get(2).getInternationalizedName());
        }
    }

    private void removeConfirmCounter(View view) {
        view.getRootView().findViewById(R.id.dynamic_tab_options_table).setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.confirm_table).setVisibility(View.GONE);
    }
}
