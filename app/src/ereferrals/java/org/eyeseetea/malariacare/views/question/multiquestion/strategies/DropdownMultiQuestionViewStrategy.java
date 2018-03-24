package org.eyeseetea.malariacare.views.question.multiquestion.strategies;

import android.content.Context;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Validation;
import org.eyeseetea.malariacare.views.question.multiquestion.DropdownMultiQuestionView;


public class DropdownMultiQuestionViewStrategy extends ADropdownMultiQuestionViewStrategy {

    public DropdownMultiQuestionViewStrategy(Context context) {
        super(context);
    }

    @Override
    public void init(DropdownMultiQuestionView dropdownMultiQuestionView) {
        TextView header = (TextView) dropdownMultiQuestionView.findViewById(R.id.row_header_text);
        Validation.getInstance().addInput(header);
        Validation.getInstance().addinvalidInput(header,
                dropdownMultiQuestionView.getContext().getString(R.string.error_empty_question));
    }

    @Override
    public void onItemSelected(DropdownMultiQuestionView dropdownMultiQuestionView, int position) {
        TextView header = (TextView) dropdownMultiQuestionView.findViewById(R.id.row_header_text);
        if (position > 0) {
            Validation.getInstance().removeInputError(header);
        }
    }
}
