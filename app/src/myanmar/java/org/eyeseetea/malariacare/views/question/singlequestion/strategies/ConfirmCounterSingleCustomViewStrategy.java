package org.eyeseetea.malariacare.views.question.singlequestion.strategies;

import static org.eyeseetea.malariacare.R.id.question;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.TextCard;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ConfirmCounterSingleCustomViewStrategy implements
        IConfirmCounterSingleCustomViewStrategy {
    DynamicTabAdapter mDynamicTabAdapter;

    private String currentCounterValue = "";

    public ConfirmCounterSingleCustomViewStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    public void showConfirmCounter(final View view, final Option selectedOption,
            Question question, Question questionCounter) {

        View rootView = view.getRootView();

        currentCounterValue = getCounterValue(question, selectedOption);

        showConfirmCounterViewAndHideCurrentQuestion(rootView);

        hideStandardButtons(rootView);

        configureNavigationButtons(view, selectedOption, question, questionCounter, rootView);

        showQuestionHeader(questionCounter, rootView);
        showQuestionImage(questionCounter, rootView);
        showQuestionText(questionCounter, rootView);

    }

    private void showConfirmCounterViewAndHideCurrentQuestion(View rootView) {
        //Show confirm on full screen
        rootView.findViewById(R.id.no_scrolled_table).setVisibility(View.GONE);
        rootView.findViewById(R.id.confirm_table).setVisibility(View.VISIBLE);
    }

    private void hideStandardButtons(View rootView) {
        TextCard standardTextYes = (TextCard) rootView.findViewById(R.id.textcard_confirm_yes);
        ImageView standardButtonYes = (ImageView) rootView.findViewById(R.id.confirm_yes);
        standardTextYes.setVisibility(View.GONE);
        standardButtonYes.setVisibility(View.GONE);
        TextCard standardTextNo = (TextCard) rootView.findViewById(R.id.textcard_confirm_no);
        ImageView standardButtonNo = (ImageView) rootView.findViewById(R.id.confirm_no);
        standardTextNo.setVisibility(View.GONE);
        standardButtonNo.setVisibility(View.GONE);
    }

    public void configureNavigationButtons(final View view, final Option selectedOption,
            final Question question, Question questionCounter, View rootView) {
        //cancel
        ImageButton previousButton = (ImageButton) rootView.findViewById(R.id.back_btn);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDynamicTabAdapter.removeConfirmCounter(v);
                mDynamicTabAdapter.notifyDataSetChanged();
            }
        });

        //confirm
        ImageButton nextButton = (ImageButton) rootView.findViewById(R.id.next_btn);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDynamicTabAdapter.removeConfirmCounter(v);
                mDynamicTabAdapter.saveOptionAndMove(view, selectedOption, question);
            }
        });

        List<Option> questionOptions = questionCounter.getAnswer().getOptions();

        if (questionOptions.get(2) != null) {
            TextCard textNextButton = (TextCard) rootView.findViewById(R.id.next_txt);
            textNextButton.setText(getInternationalizedName(questionOptions.get(2).getCode()));
            textNextButton.setTextSize(questionOptions.get(2).getOptionAttribute().getText_size());
        }
    }

    private void showQuestionHeader(Question questionCounter, View rootView) {
        final TextCard questionView = (TextCard) rootView.findViewById(question);
        questionView.setText(getInternationalizedName(questionCounter.getForm_name()));
    }

    public void showQuestionImage(Question questionCounter, View rootView) {
        if (questionCounter.getPath() != null && !questionCounter.getPath().equals("")) {
            ImageView imageView = (ImageView) rootView.findViewById(R.id.questionImageRow);
            putImageInImageView(questionCounter.getInternationalizedPath(),
                    imageView);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public void showQuestionText(Question questionCounter, View rootView) {
        List<Option> questionOptions = questionCounter.getAnswer().getOptions();
        if (questionOptions.get(0) != null) {
            TextCard textCard = (TextCard) rootView.findViewById(R.id.questionTextRow);
            textCard.setText(getInternationalizedName(questionOptions.get(0).getCode()));
            textCard.setTextSize(questionOptions.get(0).getOptionAttribute().getText_size());

            TextView subTitle = (TextView) rootView.findViewById(R.id.questionSubText);
            subTitle.setText(getInternationalizedName(questionOptions.get(0).getName()));
            subTitle.setTextSize(questionOptions.get(0).getOptionAttribute().getText_size());
        }
    }

    public void putImageInImageView(String path, ImageView imageView) {
        if (path == null || path.equals("")) {
            return;
        }
        try {
            InputStream ims = PreferencesState.getInstance().getContext().getAssets().open(path);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = DisplayMetrics.DENSITY_HIGH;
            Drawable drawable = Drawable.createFromResourceStream(
                    PreferencesState.getInstance().getContext().getResources(), null, ims, null,
                    opts);

            imageView.setImageDrawable(drawable);
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getInternationalizedName(String name) {

        if (name.contains("%s")) {
            name = String.format(name, currentCounterValue);
        }

        return Utils.getInternationalizedString(name);
    }

    private String getCounterValue(Question question, Option selectedOption) {
        Question optionCounter = question.findCounterByOption(selectedOption);

        if (optionCounter == null) {
            return "";
        }

        String counterValue = ReadWriteDB.readValueQuestion(optionCounter);
        if (counterValue == null || counterValue.isEmpty()) {
            return "1";
        }


        return String.valueOf((Integer.parseInt(counterValue) + 1));
    }
}
