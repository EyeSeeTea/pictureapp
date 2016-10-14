package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idelcano on 09/06/2016.
 */
public class ReviewFragment extends Fragment {

    public static final String TAG = ".ReviewFragment";
    private List<Value> values;
    protected IDashboardAdapter adapter;
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

        initAdapter();
        initListView(view);
        return view;
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
        values = getReviewValues();
        ReviewScreenAdapter adapterInSession = new ReviewScreenAdapter(this.values,lInflater, getActivity());
        this.adapter = adapterInSession;
    }

    private List<Value> getReviewValues() {
        List<Value> reviewValues = new ArrayList<>();
        Survey survey = Session.getSurvey();
        List<Value> allValues = survey.getValuesFromDB();
        for(Value value:allValues) {
            boolean isReviewValue=true;
            for(QuestionRelation questionRelation:value.getQuestion().getQuestionRelations()){
                if(questionRelation.isACounter() || questionRelation.isAReminder() || questionRelation.isAWarning())
                    isReviewValue=false;
            }
            if(isReviewValue)
                reviewValues.add(value);
        }
        return reviewValues;
    }

    /**
     * Initializes the listview component, adding a listener for swiping right
     */
    private void initListView(View view) {
        //inflate headers
        View header = lInflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View subHeader = lInflater.inflate(((ReviewScreenAdapter) this.adapter).getSubHeaderLayout(), null, false);

        ListView listView = (ListView) view.findViewById(R.id.review_list);

        //set headers and list in the listview
        listView.addHeaderView(header);
        listView.addHeaderView(subHeader);
        listView.setAdapter((BaseAdapter) adapter);

        //remove spaces between rows in the listview
        listView.setDividerHeight(0);
    }
}
