package org.eyeseetea.malariacare.views.question.multiquestion;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.layout.adapters.general.OptionArrayAdapter;
import org.eyeseetea.malariacare.views.TextCard;
import org.eyeseetea.malariacare.views.question.AOptionQuestionView;
import org.eyeseetea.malariacare.views.question.IMultiQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public class DropdownMultiQuestionView extends AOptionQuestionView implements IQuestionView,
        IMultiQuestionView {
    TextCard header;
    Spinner spinnerOptions;
    Question question;

    public DropdownMultiQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setOptions(List<Option> options) {
        spinnerOptions.setAdapter(new OptionArrayAdapter(getContext(), options));
    }

    @Override
    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public void setHeader(String headerValue) {
        header.setText(headerValue);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        spinnerOptions.setEnabled(enabled);
    }

    @Override
    public void setValue(Value value) {
        if (value == null || value.getValue() == null) {
            return;
        }

        for (int i = 0; i < spinnerOptions.getAdapter().getCount(); i++) {
            Option option = (Option) spinnerOptions.getItemAtPosition(i);
            if (option.equals(value.getOption())) {
                spinnerOptions.setSelection(i);
                break;
            }
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.multi_question_tab_phone_row, this);

        header = (TextCard) findViewById(R.id.row_header_text);
        spinnerOptions = (Spinner) findViewById(R.id.answer);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {


/*                Option option = (Option) parent.getItemAtPosition(position);

                notifyAnswerChanged(option.);

                Question question = (Question) parent.getTag();
                if (question.getOutput().equals(Constants.IMAGE_3_NO_DATAELEMENT)) {
                    switchHiddenMatches(question, option);
                } else {
                    ReadWriteDB.saveValuesDDL(question, option, question.getValueBySession());
                }
                showOrHideChildren(question);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
