package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.entity.MediaListMode;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;

import java.util.List;

public class MediaPresenter {

    View mView;
    private GetMediaUseCase mGetMediaUseCase;
    private Settings settings;
    List<Media> mMediaList;

    public MediaPresenter(
            GetMediaUseCase getMediaUseCase, Settings settings) {
        mGetMediaUseCase = getMediaUseCase;
        this.settings = settings;
    }

    public void attachView(final View view) {
        this.mView = view;

        loadData();
    }

    public void detachView() {
        mView = null;
    }

    private void loadData() {
        mGetMediaUseCase.execute(new GetMediaUseCase.Callback() {
            @Override
            public void onSuccess(List<Media> medias) {
                mMediaList = medias;
                if (mView != null) {
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
        if (settings.getMediaListMode().equals(MediaListMode.LIST)) {
            mView.showMediaListMode(mMediaList);
        } else if (settings.getMediaListMode().equals(MediaListMode.GRID)){
            mView.showMediaGridMode(mMediaList);
        }
    }

    public void onClickChangeMode(MediaListMode mediaListMode) {
        settings.setMediaListMode(mediaListMode);
        showMediaItems();
    }

    public void onClickMedia(Media media) {
        if (mView != null) {
            mView.OpenMedia(media.getResourcePath());
        }
    }

    public interface View {
        void showMediaGridMode(List<Media> medias);

        void showMediaListMode(List<Media> medias);

        void OpenMedia(String resourcePath);
    }

    public boolean canShowErrorMessage() {
        return mMediaList == null || mMediaList.isEmpty();
    }
}