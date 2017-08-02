package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import static org.eyeseetea.malariacare.R.id.question;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.fragments.ReviewFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.utils.BaseLayoutUtils;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class ConfirmCounterSingleCustomViewStrategy implements
        IConfirmCounterSingleCustomViewStrategy {
    private static final String TAG = ".ConfirmCounter";
    DynamicTabAdapter mDynamicTabAdapter;
    private String currentCounterValue = "";

    public ConfirmCounterSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void showConfirmCounter(final View view, final OptionDB selectedOption,
            QuestionDB questionDB, QuestionDB questionDBCounter) {
        View rootView = view.getRootView();

        currentCounterValue = getCounterValue(questionDB, selectedOption);

        showConfirmCounterViewAndHideCurrentQuestion(rootView);

        configureNavigationButtons(view, selectedOption, questionDB, questionDBCounter, rootView);

        showQuestionHeader(questionDBCounter, rootView);
        showQuestionImage(questionDBCounter, rootView);
        showQuestionText(questionDBCounter, rootView);

    }

    private void showConfirmCounterViewAndHideCurrentQuestion(View rootView) {
        //Show confirm on full screen
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);
    }

    public void configureNavigationButtons(final View view, final OptionDB selectedOption,
            final QuestionDB questionDB, QuestionDB questionDBCounter, View rootView) {
        //cancel
        ImageButton previousButton = (ImageButton) rootView.findViewById(R.id.back_btn);

        ((LinearLayout) previousButton.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DynamicTabAdapter.isClicked) {
                    Log.d(TAG, "onClick ignored to avoid double click");
                    return;
                } else {
                    Log.d(TAG, "onClick ignored to avoid double click NOT");
                }
                removeConfirmCounter(v);
                mDynamicTabAdapter.notifyDataSetChanged();
                DynamicTabAdapter.isClicked = false;
            }
        });

        //confirm
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_btn);

        ((LinearLayout) nextButton.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDynamicTabAdapter.reloadingQuestionFromInvalidOption) {
                    Log.d(TAG, "onClick ignored to avoid double click");
                    return;
                } else {
                    Log.d(TAG, "onClick");
                }
                mDynamicTabAdapter.navigationController.increaseCounterRepetitions(selectedOption);
                removeConfirmCounter(v);
                mDynamicTabAdapter.reloadingQuestionFromInvalidOption = true;
                mDynamicTabAdapter.saveOptionValue(view, selectedOption, questionDB, true);

                if (selectedOption.getFactor() == Float.parseFloat(currentCounterValue)) {
                    ReviewFragment.mLoadingReviewOfSurveyWithMaxCounter = true;
                }
            }
        });

        List<OptionDB> questionOptions = questionDBCounter.getAnswerDB().getOptionDBs();

        if (questionOptions.get(2) != null) {
            CustomTextView textNextButton = (CustomTextView) rootView.findViewById(R.id.next_txt);
            textNextButton.setText(getInternationalizedName(questionOptions.get(2).getName()));
            textNextButton.setTextSize(questionOptions.get(2).getOptionAttributeDB().getText_size());
        }
    }

    private void showQuestionHeader(QuestionDB questionDBCounter, View rootView) {
        final CustomTextView questionView = (CustomTextView) rootView.findViewById(question);
        questionView.setText(getInternationalizedName(questionDBCounter.getForm_name()));
    }

    public void showQuestionImage(QuestionDB questionDBCounter, View rootView) {
        if (questionDBCounter.getPath() != null && !questionDBCounter.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            BaseLayoutUtils.putImageInImageViewDensityHigh(
                    questionDBCounter.getInternationalizedPath(),
                    imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void showQuestionText(QuestionDB questionDBCounter, View rootView) {
        List<OptionDB> questionOptions = questionDBCounter.getAnswerDB().getOptionDBs();
        if (questionOptions.get(0) != null) {
            CustomTextView textCard = (CustomTextView) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(getInternationalizedName(questionOptions.get(0).getName()));
            textCard.setTextSize(questionOptions.get(0).getOptionAttributeDB().getText_size());

            TextView subTitle = (TextView) rootView.findViewById(R.id.questionSubText);
            subTitle.setText(getInternationalizedName(questionOptions.get(0).getCode()));
            subTitle.setTextSize(questionOptions.get(0).getOptionAttributeDB().getText_size());
        }
    }


    private String getInternationalizedName(String name) {
        if (name.contains("%s")) {
            name = String.format(name, currentCounterValue);
        }

        return Utils.getInternationalizedString(name);
    }

    private String getCounterValue(QuestionDB questionDB, OptionDB selectedOption) {
        QuestionDB optionCounter = questionDB.findCounterByOption(selectedOption);

        if (optionCounter == null) {
            return "";
        }

        String counterValue = optionCounter.getQuestionValueBySession();
        if (counterValue == null || counterValue.isEmpty()) {
            return "1";
        }

        return String.valueOf((Integer.parseInt(counterValue) + 1));
    }

    private void removeConfirmCounter(View view) {
        view.getRootView().findViewById(R.id.dynamic_tab_options_table).setVisibility(View.VISIBLE);
        view.getRootView().findViewById(R.id.confirm_table).setVisibility(View.GONE);
    }
}
