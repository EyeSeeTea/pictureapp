package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.boundary.IStylePreferencesRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.GetMediaUseCase;

import java.util.List;

public class MediaPresenter {

    View mView;
    private GetMediaUseCase mGetMediaUseCase;
    private IStylePreferencesRepository.ListStyle listStyle;
    List<Media> mMediaList;

    public MediaPresenter(
            GetMediaUseCase getMediaUseCase, IStylePreferencesRepository.ListStyle listStyle) {
        mGetMediaUseCase = getMediaUseCase;
        this.listStyle = listStyle;
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
        if (listStyle.equals(IStylePreferencesRepository.ListStyle.LIST)) {
            mView.showMediaListMode(mMediaList);
        } else if (listStyle.equals(IStylePreferencesRepository.ListStyle.GRID)){
            mView.showMediaGridMode(mMediaList);
        }
    }

    public void onClickChangeMode(IStylePreferencesRepository.ListStyle listStyle) {
        this.listStyle = listStyle;
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