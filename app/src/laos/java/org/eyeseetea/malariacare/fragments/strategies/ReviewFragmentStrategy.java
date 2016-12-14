package org.eyeseetea.malariacare.fragments.strategies;

import android.view.View;

import org.eyeseetea.malariacare.fragments.ReviewFragment;

public class ReviewFragmentStrategy implements IReviewFragmentStrategy {

    private ReviewFragment mReviewFragment;

    public ReviewFragmentStrategy(
            ReviewFragment reviewFragment) {
        mReviewFragment = reviewFragment;
    }

    @Override
    public void OnCreateView(View rootView) {
    }
}
