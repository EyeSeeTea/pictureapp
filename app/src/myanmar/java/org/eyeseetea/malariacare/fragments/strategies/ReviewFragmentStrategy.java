package org.eyeseetea.malariacare.fragments.strategies;

import android.view.View;
import android.widget.ImageView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.fragments.ReviewFragment;

public class ReviewFragmentStrategy implements IReviewFragmentStrategy {

    private ReviewFragment mReviewFragment;

    public ReviewFragmentStrategy(
            ReviewFragment reviewFragment) {
        mReviewFragment = reviewFragment;
    }

    @Override
    public void OnCreateView(View rootView) {

        Survey survey = Session.getSurvey();

        //TODO: After the release try to know if it has reached the limit of counters asking the
        // survey not by using static public variable in the fragment
        if (mReviewFragment.mLoadingReviewOfSurveyWithMaxCounter || survey.isSent()) {

            HideCancelButton(rootView);

            mReviewFragment.mLoadingReviewOfSurveyWithMaxCounter = false;
        }
    }

    public void HideCancelButton(View rootView) {
        ImageView okButton = (ImageView) rootView.findViewById(R.id.review_image_button);

        ImageView cancelButton = (ImageView) rootView.findViewById(R.id.review_cancel_button);

        okButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        cancelButton.setVisibility(View.GONE);
    }
}
