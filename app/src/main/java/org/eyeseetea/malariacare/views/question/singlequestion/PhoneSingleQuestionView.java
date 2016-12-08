package org.eyeseetea.malariacare.views.question.singlequestion;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.domain.entity.Phone;
import org.eyeseetea.malariacare.domain.exception.InvalidPhoneException;
import org.eyeseetea.malariacare.views.EditCard;
import org.eyeseetea.malariacare.views.question.AKeyboardSingleQuestionView;
import org.eyeseetea.malariacare.views.question.IQuestionView;

public class PhoneSingleQuestionView extends AKeyboardSingleQuestionView implements IQuestionView {
    EditCard editCard;
    Button sendButton;

    public PhoneSingleQuestionView(Context context) {
        super(context);

        init(context);
    }

    @Override
    public void setEnabled(boolean enabled) {
        editCard.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            showKeyboard(editCard);
        }
    }

    @Override
    public void setImage(String path) {
        return;
    }

    @Override
    public void setValue(Value value) {
        if (value != null) {
            editCard.setText(value.getValue());
        }
    }

    private void init(final Context context) {
        inflate(context, R.layout.dynamic_tab_phone_row, this);

        editCard = (EditCard) findViewById(R.id.answer);
        editCard.setFocusable(true);
        editCard.setFocusableInTouchMode(true);

        sendButton = (Button) findViewById(R.id.row_phone_btn);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(context);
            }
        });

        editCard.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateAnswer(context);
                    return true;
                }

                return false;
            }
        });

/*        if (!isMultipleQuestionTab(tabType)) {
            //Add button to listener
            swipeTouchListener.addClickableView(button);
        }*/
    }

    private void validateAnswer(Context context) {
        try {
            Phone phone = new Phone(editCard.getText().toString());
            hideKeyboard(editCard);
            notifyAnswerChanged(phone.getValue());

        } catch (InvalidPhoneException e) {
            editCard.setError(
                    context.getString(R.string.dynamic_error_phone_format));
        }
    }

}
