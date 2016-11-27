package org.eyeseetea.malariacare.layout.adapters.survey.navigation;

import android.util.Log;

import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.utils.ReadWriteDB;

/**
 * POJO that represents a counter question, is never shown but its value is increased
 * with every repetition of a given question + option
 * Created by arrizabalaga on 1/07/16.
 */
public class QuestionCounter {

    private static final String TAG = ".QuestionCounter";
    private final Integer infinite_counter_number = 0;
    /**
     * The question whose value will be increased
     */
    private Question counterQuestion;

    public QuestionCounter(Question counterQuestion) {
        this.counterQuestion = counterQuestion;
    }

    public void increaseRepetitions() {
        String currentRepetitionsStr = ReadWriteDB.readValueQuestion(counterQuestion);
        Integer increasedRepetitions = toInteger(currentRepetitionsStr) + 1;
        ReadWriteDB.saveValuesText(counterQuestion, increasedRepetitions.toString());
        Log.i(TAG, String.format("Counter %s updated, current value %d", counterQuestion.getCode(),
                increasedRepetitions));
    }

    //Limits the counter by a number of failed attempts.
    public boolean isMaxCounterLimit(Integer limit) {
        Integer currentRepetitions = toInteger(ReadWriteDB.readValueQuestion(counterQuestion));
        Log.i(TAG, String.format("Counter %s updated, current value %d", counterQuestion.getCode(),
                currentRepetitions));
        return !(limit == infinite_counter_number || currentRepetitions != limit);
    }

    private int toInteger(String currentRepetitions) {
        if (currentRepetitions == null || currentRepetitions.isEmpty()) {
            return 0;
        }
        try {
            return Integer.valueOf(currentRepetitions);
        } catch (NumberFormatException ex) {
            Log.e(TAG,
                    String.format("Counter %s cannot be updated, current value '%s' not a integer",
                            counterQuestion.getCode(), currentRepetitions));
            return 0;
        }
    }
}
