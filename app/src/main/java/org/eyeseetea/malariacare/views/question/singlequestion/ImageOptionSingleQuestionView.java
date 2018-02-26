package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.views.option.ImageOptionView;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.ArrayList;
import java.util.List;

public class ImageOptionSingleQuestionView extends AOptionQuestionView implements
        IQuestionView, ImageOptionView.OnOptionSelectedListener {

    TableLayout mImageOptionsContainer;
    QuestionDB mQuestionDB;

    List<ImageOptionView> mImageOptionViews = new ArrayList<>();
    private int mColumnsCount = 1;
    private int mTotalOptions = 1;

    public ImageOptionSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<OptionDB> optionDBs) {
        TableRow tableRow = null;
        mTotalOptions = optionDBs.size();
        for (int i = 0; i < optionDBs.size(); i++) {
            OptionDB optionDB = optionDBs.get(i);

            if (isNewRow(i)) {
                tableRow = new TableRow(getContext());

                tableRow.setLayoutParams(
                        new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.MATCH_PARENT, 1));

                mImageOptionsContainer.addView(tableRow);
            }

            ImageOptionView imageOptionView = createOptionView(optionDB);

            tableRow.addView(imageOptionView);
            mImageOptionViews.add(imageOptionView);
        }
    }

    public void setColumnsCount(int columnsCount) {
        mColumnsCount = columnsCount;
    }

    public void setQuestionDB(QuestionDB questionDB) {
        mQuestionDB = questionDB;
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
    public void setValue(ValueDB valueDB) {
        if (valueDB == null || valueDB.getValue() == null) {
            return;
        }

        for (int i = 0; i < mImageOptionViews.size(); i++) {
            ImageOptionView imageOptionView = mImageOptionViews.get(i);

            boolean selected = imageOptionView.getOptionDB().equals(valueDB.getOptionDB());

            imageOptionView.setSelectedOption(selected);
        }
    }

    public boolean isNewRow(int i) {
        return i % mColumnsCount == 0;
    }

    @NonNull
    private ImageOptionView createOptionView(OptionDB optionDB) {
        ImageOptionView imageOptionView = new ImageOptionView(getContext(), mColumnsCount,
                mTotalOptions);
        imageOptionView.setOption(optionDB, mQuestionDB);
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
    public void onOptionSelected(View view, OptionDB optionDB) {
        notifyAnswerChanged(optionDB);
    }
}
