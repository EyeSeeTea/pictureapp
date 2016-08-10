package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.listeners.SwipeTouchListener;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.List;

/**
 * Created by idelcano on 09/06/2016.
 */
public class ReviewFragment extends Fragment {

    public static final String TAG = ".ReviewFragment";

    LayoutInflater lInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        this.lInflater = LayoutInflater.from(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.review_layout,
                container, false);
        initValues(view);
        view.setOnTouchListener(new SwipeTouchListener(view.getContext()) {
            @Override
            public void onSwipeRight() {
                DashboardActivity.dashboardActivity.hideReview();
            }
        });
        return view;
    }

    /**
     * Inflate a TextCard inside a linearLayout for each value
     * @param view
     */
    private void initValues(View view) {
        Survey survey= Session.getSurvey();
        List<Value> values = survey.getValuesFromDB();
        LinearLayout linearLayout=(LinearLayout)view.findViewById(R.id.options_review_table);
        for(Value value:values) {
            boolean isReviewValue=true;
            for(QuestionRelation questionRelation:value.getQuestion().getQuestionRelations()){
                if(questionRelation.isACounter() || questionRelation.isAReminder() || questionRelation.isAWarning())
                    isReviewValue=false;
            }
            if(isReviewValue)
                drawValue(linearLayout, value);
        }
    }


    /**
     * Inflate the linearlayout to add the values
     * @param linearLayout
     * @param value
     */
    private void drawValue(LinearLayout linearLayout, Value value) {
        TextCard textCard=(TextCard) lInflater.inflate(R.layout.dynamic_review_row,linearLayout,false);
        textCard.setText(value.getValue());
        if(value.getQuestion()!=null) {
            if(value.getOption()!=null && value.getOption().getBackground_colour()!=null)
                textCard.setBackgroundColor(Color.parseColor("#" + value.getOption().getBackground_colour()));
            linearLayout.addView(textCard);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }
}
