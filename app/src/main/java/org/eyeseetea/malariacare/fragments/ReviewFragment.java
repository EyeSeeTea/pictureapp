package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.common.collect.Iterables;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.strategies.ReviewFragmentStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReviewFragment extends Fragment {

    public static final String TAG = ".ReviewFragment";
    public static boolean mLoadingReviewOfSurveyWithMaxCounter;
    protected IDashboardAdapter adapter;
    LayoutInflater lInflater;
    private List<Value> values;
    private OnEndReviewListener mOnEndReviewListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        this.lInflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.review_layout,
                container, false);
        initAdapter();
        initListView(view);
        initReviewButton(view);
        return view;
    }

    private void initReviewButton(View view) {
        view.findViewById(R.id.review_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEndReviewListener != null) {
                    mOnEndReviewListener.onEndReview();
                }
            }
        });
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

    /**
     * Inits reviewfragment adapter.
     */
    private void initAdapter() {
        ReviewScreenAdapter adapterInSession = new ReviewScreenAdapter(prepareValues(), lInflater,
                getActivity());
        this.adapter = adapterInSession;
    }

    private List<org.eyeseetea.malariacare.domain.entity.Value> prepareValues() {
        Iterator<String> colorIterator;
        List<org.eyeseetea.malariacare.domain.entity.Value> preparedValues = new ArrayList<>();
        values = getReviewValues();
        values = orderValues(values);
        colorIterator = Iterables.cycle(createBackgroundColorList()).iterator();
        for(Value value:values) {
            org.eyeseetea.malariacare.domain.entity.Value preparedValue =new org.eyeseetea.malariacare.domain.entity.Value(value.getValue());
            if(value.getQuestion()!=null)
            preparedValue.setQuestionUId(value.getQuestion().getUid());
            if(value.getOption()!=null)
            preparedValue.setInternationalizedCode(value.getOption().getInternationalizedCode());
            if(colorIterator.hasNext()) {
                preparedValue.setBackgroundColor(colorIterator.next());
            }
            preparedValues.add(preparedValue);
        }
        return preparedValues;
    }

    private List<String> createBackgroundColorList() {
        List<String> colorsList = new ArrayList<>();
        for(Value value:values) {
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

    private List<Value> orderValues(List<Value> values) {
        ReviewFragmentStrategy reviewFragmentStrategy = new ReviewFragmentStrategy();
        return reviewFragmentStrategy.orderValues(values);
    }

    private List<Value> getReviewValues() {
        List<Value> reviewValues = new ArrayList<>();
        Survey survey = Session.getMalariaSurvey();
        List<Value> allValues = survey.getValuesFromDB();
        for (Value value : allValues) {
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
                if (value.getQuestion()!=null) {
                    reviewValues.add(value);
                }
            }
        }
        return reviewValues;
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView(View view) {
        //inflate headers
        View header = lInflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View subHeader = lInflater.inflate(
                ((ReviewScreenAdapter) this.adapter).getSubHeaderLayout(), null, false);

        ListView listView = (ListView) view.findViewById(R.id.review_list);

        //set headers and list in the listview
        listView.addHeaderView(header);
        listView.addHeaderView(subHeader);
        listView.setAdapter((BaseAdapter) adapter);

        //remove spaces between rows in the listview
        listView.setDividerHeight(0);
    }

    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
    }

    public void setOnEndReviewListener(
            OnEndReviewListener onEndReviewListener) {
        mOnEndReviewListener = onEndReviewListener;
    }

    public interface OnEndReviewListener {
        void onEndReview();
    }
}
