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

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.ValueLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.GetReviewValuesBySurveyIdUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presenter.ReviewPresenter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReviewFragment extends Fragment implements ReviewPresenter.ReviewView {

    public static final String TAG = ".ReviewFragment";
    public static boolean mLoadingReviewOfSurveyWithMaxCounter;
    protected IDashboardAdapter adapter;
    LayoutInflater lInflater;
    View mView;
    ListView listView;
    ReviewPresenter mReviewPresenter;
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
        mView = inflater.inflate(R.layout.review_layout,
                container, false);
        listView = (ListView) mView.findViewById(R.id.review_list);
        initializePresenter();
        initReviewButton(mView);
        return mView;
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


    @Override
    public void onDestroy() {

        if (mReviewPresenter != null) {
            mReviewPresenter.detachView();
        }

        super.onDestroy();
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
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IValueRepository valueLocalDataSource = new ValueLocalDataSource();
        GetReviewValuesBySurveyIdUseCase getReviewValuesBySurveyIdUseCase =
                new GetReviewValuesBySurveyIdUseCase(mainExecutor, asyncExecutor,
                        valueLocalDataSource);
        mReviewPresenter = new ReviewPresenter(getReviewValuesBySurveyIdUseCase);
        mReviewPresenter.attachView(this, Session.getMalariaSurvey().getId_survey());
    }

    @Override
    public void showValues(List<org.eyeseetea.malariacare.domain.entity.Value> values) {
        Iterator<String> colorIterator = Iterables.cycle(
                createBackgroundColorList(values)).iterator();
        for (Value value : values) {
            value.setBackgroundColor(colorIterator.next());
        }
        ReviewScreenAdapter adapterInSession = new ReviewScreenAdapter(values, lInflater,
                getActivity(), new ReviewScreenAdapter.onClickListener() {
            @Override
            public void onClickOnValue(String UId) {
                mReviewPresenter.onClickOnValue(UId);
            }
        });
        this.adapter = adapterInSession;
    }

    private List<String> createBackgroundColorList(
            List<Value> values) {
        List<java.lang.String> colorsList = new ArrayList<>();
        for (Value value : values) {
            if (value.getBackgroundColor() != null) {
                java.lang.String color = value.getBackgroundColor();
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


    public void setOnEndReviewListener(
            OnEndReviewListener onEndReviewListener) {
        mOnEndReviewListener = onEndReviewListener;
    }

    public interface OnEndReviewListener {
        void onEndReview();
    }

    @Override
    public void navigateToQuestion(String uId) {
        DashboardActivity.dashboardActivity.hideReview(uId);
    }
}
