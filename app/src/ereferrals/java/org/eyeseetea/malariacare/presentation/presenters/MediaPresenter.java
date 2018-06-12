package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository.MediaListMode;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSettingsUseCase;

import java.util.List;

public class MediaPresenter {

    View mView;
    private GetMediaUseCase mGetMediaUseCase;
    private GetSettingsUseCase mGetSettingsUseCase;
    private SaveSettingsUseCase mSaveSettingsUseCase;
    List<Media> mMediaList;

    public MediaPresenter(
            GetMediaUseCase getMediaUseCase, GetSettingsUseCase getSettingsUseCase, SaveSettingsUseCase saveSettingsUseCase) {
        mGetMediaUseCase = getMediaUseCase;
        mGetSettingsUseCase = getSettingsUseCase;
        mSaveSettingsUseCase = saveSettingsUseCase;
    }

    public void attachView(final View view) {
        this.mView = view;

        loadData();
    }

    public void detachView() {
        mView = null;
    }

    private void loadData() {
        mGetSettingsUseCase.execute(new GetSettingsUseCase.Callback(){

            @Override
            public void onSuccess(final Settings setting) {
                mGetMediaUseCase.execute(new GetMediaUseCase.Callback() {
                    @Override
                    public void onSuccess(List<Media> medias) {
                        mMediaList = medias;
                        if (mView != null) {
                            showMediaItems(setting);
                        }
                    }

                    @Override
                    public void onError() {
                        System.out.println("Error getting media");
                    }
                });
            }
        });
    }

    private void showMediaItems(Settings settings) {
        if (settings.getMediaListMode().equals(MediaListMode.LIST)) {
            mView.showMediaListMode(mMediaList);
        } else if (settings.getMediaListMode().equals(MediaListMode.GRID)){
            mView.showMediaGridMode(mMediaList);
        }
    }

    public void onClickChangeMode(final MediaListMode mediaListMode) {
        mGetSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(final Settings setting) {
                setting.setMediaListMode(mediaListMode);
                mSaveSettingsUseCase.execute(new SaveSettingsUseCase.Callback() {
                    @Override
                    public void onSuccess() {
                        showMediaItems(setting);
                    }
                }, setting);
            }
        });
    }

    public void onClickMedia(Media media) {
        if (mView != null) {
            mView.openMedia(media.getResourcePath());
        }
    }

    public interface View {
        void showMediaGridMode(List<Media> medias);

        void showMediaListMode(List<Media> medias);

        void openMedia(String resourcePath);
    }

    public boolean canShowErrorMessage() {
        return mMediaList == null || mMediaList.isEmpty();
    }
}