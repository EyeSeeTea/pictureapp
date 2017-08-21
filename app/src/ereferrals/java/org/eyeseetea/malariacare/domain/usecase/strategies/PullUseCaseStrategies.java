package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.strategies.APullUseCaseStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.sdk.common.FileUtils;

import java.util.List;


public class PullUseCaseStrategies extends APullUseCaseStrategy {


    public PullUseCaseStrategies(
            PullUseCase pullUseCase) {
        super(pullUseCase);
    }

    @Override
    public void onPullComplete() {
        IProgramRepository programRepository = new ProgramLocalDataSource();
        IMediaRepository mediaRepository = new MediaRepository();
        List<Media> mediaList = mediaRepository.getAll();
        if(mediaList==null || mediaList.size()==0){
            mPullUseCase.notifyComplete();
            return;
        }
        Media media = mediaList.get(0);
        if(media!=null && media.getProgram()!=null && !media.getProgram()
                .equals(programRepository.getUserProgram().getId())){
            System.out.println("The media data will be removed");
            mediaRepository.clearMedia();
            FileUtils.removeDir(
                    PreferencesState.getInstance().getContext().getFilesDir().getAbsolutePath()+"/"+Constants.MEDIA_FOLDER);
        }
        mPullUseCase.notifyComplete();
    }

    @Override
    public void onOnNetworkError() {
        if (PreferencesEReferral.getUserProgramId() != -1) {
            mPullUseCase.notifyComplete();
        } else {
            mPullUseCase.notifyOnNetworkError();
        }
    }
}
