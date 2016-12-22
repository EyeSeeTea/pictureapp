package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.layout.adapters.question.ImageQuestionOptionsAdapter;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class ImageOptionSingleQuestionView extends AOptionQuestionView implements
        IQuestionView {

    RecyclerView mImageOptionsRecyclerView;
    ImageQuestionOptionsAdapter mImageQuestionOptionsAdapter;

    public ImageOptionSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<Option> options) {
        mImageQuestionOptionsAdapter = new ImageQuestionOptionsAdapter(options);

        mImageOptionsRecyclerView.setAdapter(mImageQuestionOptionsAdapter);
    }

    @Override
    public void setQuestion(Question question) {

    }

    @Override
    public void setEnabled(boolean enabled) {
        mImageOptionsRecyclerView.setEnabled(enabled);
    }

    @Override
    public void setHelpText(String helpText) {

    }

    @Override
    public void setValue(Value value) {


    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_row_images_question, this);

        mImageOptionsRecyclerView = (RecyclerView) findViewById(R.id.answer);

        mImageOptionsRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    }
}
