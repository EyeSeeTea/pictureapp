package org.eyeseetea.malariacare.presenter;

import com.google.common.collect.Iterables;

import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.strategies.ReviewFragmentStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReviewPresenter {
    ReviewView view;

    public ReviewPresenter() {
    }

    public void attachView(ReviewView reviewView) {
        this.view = reviewView;
        view.showValues(prepareValues());
        view.initListView();
    }

    private List<org.eyeseetea.malariacare.domain.entity.Value> prepareValues() {
        Iterator<String> colorIterator;
        List<org.eyeseetea.malariacare.domain.entity.Value> preparedValues = new ArrayList<>();
        List<org.eyeseetea.malariacare.data.database.model.Value> values;
        values = getReviewValues();
        values = orderValues(values);
        colorIterator = Iterables.cycle(createBackgroundColorList(values)).iterator();
        for (org.eyeseetea.malariacare.data.database.model.Value value : values) {
            org.eyeseetea.malariacare.domain.entity.Value preparedValue =
                    new org.eyeseetea.malariacare.domain.entity.Value(value.getValue());
            if (value.getQuestion() != null) {
                preparedValue.setQuestionUId(value.getQuestion().getUid());
            }
            if (value.getOption() != null) {
                preparedValue.setInternationalizedCode(
                        value.getOption().getInternationalizedCode());
            }
            if (colorIterator.hasNext()) {
                preparedValue.setBackgroundColor(colorIterator.next());
            }
            preparedValues.add(preparedValue);
        }
        return preparedValues;
    }

    private List<String> createBackgroundColorList(
            List<org.eyeseetea.malariacare.data.database.model.Value> values) {
        List<String> colorsList = new ArrayList<>();
        for (org.eyeseetea.malariacare.data.database.model.Value value : values) {
            if (value.getOption() != null && value.getOption().getBackground_colour() != null) {
                String color = "#" + value.getOption().getBackground_colour();
                if (!colorsList.contains(color)) {
                    colorsList.add(color);
                }
            }
        }
        //Hardcoded colors for a colorList without colors.
        if (colorsList.size() == 0) {
            colorsList.add("#4d3a4b");
        }
        if (colorsList.size() == 1 && values.size() > 1) {
            colorsList.add("#9c7f9b");
        }
        return colorsList;
    }

    private List<org.eyeseetea.malariacare.data.database.model.Value> orderValues(
            List<org.eyeseetea.malariacare.data.database.model.Value> values) {
        ReviewFragmentStrategy reviewFragmentStrategy = new ReviewFragmentStrategy();
        return reviewFragmentStrategy.orderValues(values);
    }

    private List<org.eyeseetea.malariacare.data.database.model.Value> getReviewValues() {
        List<org.eyeseetea.malariacare.data.database.model.Value> reviewValues = new ArrayList<>();
        Survey survey = Session.getMalariaSurvey();
        List<org.eyeseetea.malariacare.data.database.model.Value> allValues =
                survey.getValuesFromDB();
        for (org.eyeseetea.malariacare.data.database.model.Value value : allValues) {
            boolean isReviewValue = true;
            if (value.getQuestion() == null) {
                continue;
            }
            for (QuestionRelation questionRelation : value.getQuestion().getQuestionRelations()) {
                if (questionRelation.isACounter() || questionRelation.isAReminder()
                        || questionRelation.isAWarning() || questionRelation.isAMatch()) {
                    isReviewValue = false;
                }
            }
            int output = value.getQuestion().getOutput();
            if (output == Constants.HIDDEN
                    || output == Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON) {
                isReviewValue = false;
            }
            if (isReviewValue) {
                if (value.getQuestion() != null) {
                    reviewValues.add(value);
                }
            }
        }
        return reviewValues;
    }

    public interface ReviewView {
        void showValues(List<Value> movies);

        void initListView();
    }
}
