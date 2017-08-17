package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;

import java.util.List;

public class MediaPresenter {

    View mView;
    private GetMediaUseCase mGetMediaUseCase;
    private boolean isListMode = true;
    List<Media> mMediaList;

    public MediaPresenter(
            GetMediaUseCase getMediaUseCase) {
        mGetMediaUseCase = getMediaUseCase;
    }

    public void attachView(final View view) {
        this.mView = view;

        loadData(view);
    }

    public void detachView() {
        mView = null;
    }

    private void loadData(final View view) {
        mGetMediaUseCase.execute(new GetMediaUseCase.Callback() {
            @Override
            public void onSuccess(List<Media> medias) {
                mMediaList = medias;
                if (view != null) {
                    showMediaItems();
                }
            }

            @Override
            public void onError() {
                System.out.println("Error getting media");
            }
        });
    }

    private void showMediaItems() {
        if (isListMode) {
            mView.showMediaListMode(mMediaList);
        } else {
            mView.showMediaGridMode(mMediaList);
        }
    }

    public void onClickChangeMode() {
        isListMode = !isListMode;
        showMediaItems();
    }

    public interface View {
        void showMediaGridMode(List<Media> medias);

        void showMediaListMode(List<Media> medias);
    }
}