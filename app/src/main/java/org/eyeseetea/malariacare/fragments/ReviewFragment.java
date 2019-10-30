package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.common.collect.Iterables;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.CompletionSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSurveyByUidUseCase;
import org.eyeseetea.malariacare.factories.SurveyFactory;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.presentation.presenters.ReviewPresenter;
import org.eyeseetea.malariacare.strategies.AReviewFragmentStrategy;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.strategies.ReviewFragmentStrategy;

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
    private AReviewFragmentStrategy mReviewFragmentStrategy;

    private static String SURVEY_UID = "sureveyUid";

    public static ReviewFragment newInstance(String surveyUid) {
        ReviewFragment reviewFragment = new ReviewFragment();

        Bundle args = new Bundle();
        args.putString(SURVEY_UID, surveyUid);
        reviewFragment.setArguments(args);

        return reviewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mReviewFragmentStrategy = new ReviewFragmentStrategy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        this.lInflater = LayoutInflater.from(getActivity());
        mView = inflater.inflate(R.layout.review_layout,
                container, false);
        listView = (ListView) mView.findViewById(R.id.review_list);

        String surveyUid = getArguments().getString(SURVEY_UID);

        initializePresenter(surveyUid);
        initReviewButton(mView);
        return mView;
    }

    private void initReviewButton(View view) {
        view.findViewById(R.id.review_image_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReviewPresenter.ok();
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
        mReviewFragmentStrategy.initListView(lInflater, adapter, listView);
    }


    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().hideHeader(activity);
    }

    private void initializePresenter(String surveyUid) {
        GetSurveyByUidUseCase getSurveyByUidUseCase = new SurveyFactory().getSurveyByUidUseCase();

        CompletionSurveyUseCase completionSurveyUseCase =
                new SurveyFactory().getCompletionSurveyUseCase();

        mReviewPresenter = new ReviewPresenter(getSurveyByUidUseCase, completionSurveyUseCase);
        mReviewPresenter.attachView(this, surveyUid);
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
        return mReviewFragmentStrategy.createBackgroundColorList(values, getActivity());
    }


    public void setOnEndReviewListener(OnEndReviewListener onEndReviewListener) {
        mOnEndReviewListener = onEndReviewListener;
    }

    public interface OnEndReviewListener {
        void onEndReview(String surveyUid, boolean afterCompletion);
    }

    @Override
    public void navigateToQuestion(String uId) {
        DashboardActivity.dashboardActivity.hideReview(uId);
    }

    @Override
    public void exit(String surveyUid, boolean afterCompletion) {
        if (mOnEndReviewListener != null){
            mOnEndReviewListener.onEndReview(surveyUid, afterCompletion);
        }
    }
}
