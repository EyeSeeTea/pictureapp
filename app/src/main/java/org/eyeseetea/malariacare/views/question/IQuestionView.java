package org.eyeseetea.malariacare.views.question;

import org.eyeseetea.malariacare.database.model.Value;

public interface IQuestionView {
    void setEnabled(boolean enabled);

    void setValue(Value value);
}
