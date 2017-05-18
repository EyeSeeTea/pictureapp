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

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.presenter.ReviewPresenter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;

import java.util.List;

public class ReviewFragment extends Fragment implements ReviewPresenter.ReviewView {

    public static final String TAG = ".ReviewFragment";
    public static boolean mLoadingReviewOfSurveyWithMaxCounter;
    protected IDashboardAdapter adapter;
    LayoutInflater lInflater;
    View mView;
    ListView listView;
    ReviewPresenter mReviewPresenter;

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
        mView = inflater.inflate(R.layout.review_layout,
                container, false);
        listView = (ListView) mView.findViewById(R.id.review_list);
        initializePresenter();
        return mView;
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
     * Initializes the listview component, adding a listener for swiping right
     */
    @Override
    public void initListView() {
        //inflate headers
        View header = lInflater.inflate(this.adapter.getHeaderLayout(), null, false);
        View subHeader = lInflater.inflate(
                ((ReviewScreenAdapter) this.adapter).getSubHeaderLayout(), null, false);

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

    private void initializePresenter() {
        mReviewPresenter = new ReviewPresenter();
        mReviewPresenter.attachView(this);
    }

    @Override
    public void showValues(List<org.eyeseetea.malariacare.domain.entity.Value> values) {
        ReviewScreenAdapter adapterInSession = new ReviewScreenAdapter(values, lInflater,
                getActivity());
        this.adapter = adapterInSession;
    }
}
