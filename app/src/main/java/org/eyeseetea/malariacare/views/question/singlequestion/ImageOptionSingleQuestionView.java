package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.views.option.ImageOptionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.ArrayList;
import java.util.List;

public class ImageOptionSingleQuestionView extends AOptionQuestionView implements
        IQuestionView, ImageOptionView.OnOptionSelectedListener {

    TableLayout mImageOptionsContainer;
    Question mQuestion;

    List<ImageOptionView> mImageOptionViews = new ArrayList<>();
    private int mColumnsCount = 1;

    public ImageOptionSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<Option> options) {
        TableRow tableRow = null;

        for (int i = 0; i < options.size(); i++) {
            Option option = options.get(i);

            if (isNewRow(i)) {
                tableRow = new TableRow(getContext());

                tableRow.setLayoutParams(
                        new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.MATCH_PARENT, 1));

                mImageOptionsContainer.addView(tableRow);
            }

            ImageOptionView imageOptionView = createOptionView(option);

            tableRow.addView(imageOptionView);
            mImageOptionViews.add(imageOptionView);
        }
    }

    public void setColumnsCount(int columnsCount) {
        mColumnsCount = columnsCount;
    }

    @Override
    public void setQuestion(Question question) {
        mQuestion = question;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        for (int i = 0; i < mImageOptionViews.size(); i++) {
            ImageOptionView imageOptionView = mImageOptionViews.get(i);

            imageOptionView.setEnabled(enabled);
        }
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < mImageOptionViews.size(); i++) {
            ImageOptionView imageOptionView = mImageOptionViews.get(i);

            boolean selected = imageOptionView.getOption().equals(value.getOption());

            imageOptionView.setSelectedOption(selected);
        }
    }

    public boolean isNewRow(int i) {
        return i % mColumnsCount == 0;
    }

    @NonNull
    private ImageOptionView createOptionView(Option option) {
        ImageOptionView imageOptionView = new ImageOptionView(getContext());
        imageOptionView.setOption(option, mQuestion);
        imageOptionView.setOnOptionSelectedListener(this);
        imageOptionView.setEnabled(isEnabled());

        imageOptionView.setLayoutParams(
                new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT, 1));

        return imageOptionView;
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_row_images_question, this);

        mImageOptionsContainer = (TableLayout) findViewById(R.id.answer);
    }

    @Override
    public void onOptionSelected(View view, Option option) {
        notifyAnswerChanged(option);
    }
}
