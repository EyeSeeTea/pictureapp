package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.identifiers.CodeGenerator;
import org.eyeseetea.malariacare.domain.identifiers.UIDGenerator;
import org.eyeseetea.malariacare.domain.service.VoucherSuffixDomainService;

import java.util.Date;
import java.util.List;

public class CompletionSurveyUseCase implements UseCase  {

    private IAsyncExecutor asyncExecutor;
    private IMainExecutor mainExecutor;
    private ISurveyRepository surveyRepository;
    private IQuestionRepository questionRepository;
    private String surveyUid;
    CompletionSurveyCallback callback;

    public CompletionSurveyUseCase (
            IAsyncExecutor asyncExecutor,
            IMainExecutor mainExecutor,
            ISurveyRepository surveyRepository,
            IQuestionRepository questionRepository){
        this.asyncExecutor = asyncExecutor;
        this.mainExecutor = mainExecutor;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
    }

    public void execute(String surveyUid, CompletionSurveyCallback callback) {
        this.surveyUid = surveyUid;
        this.callback = callback;

        asyncExecutor.run(this);
    }

    @Override
    public void run() {
        try{
            Survey survey = surveyRepository.getSurveyByUid(surveyUid);

            if (survey.isCompleted()) {
                List<Question> questions =
                        questionRepository.getQuestionsByProgram(survey.getProgramUid());

                String voucherSuffix = new VoucherSuffixDomainService().calculate(survey, questions);

                UIDGenerator uidGenerator = new UIDGenerator();

                survey.assignVoucherUid(String.valueOf(uidGenerator.generateUID()));
                survey.assignVisibleVoucherUid(survey.getVoucherUid() + voucherSuffix);
                survey.changeEventDate(new Date(uidGenerator.getTimeGeneratedUID()));

                surveyRepository.save(survey);

                notifyCompletionSuccess(survey);
            }
        } catch (Exception e){
            notifyCompletionError(e);
        }
    }

    private void  notifyCompletionSuccess(final Survey survey){
        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                callback.CompletionSurveySuccess(survey);
            }
        });
    }

    private void  notifyCompletionError(final Exception e){
        mainExecutor.run(new Runnable() {
            @Override
            public void run() {
                callback.CompletionSurveyError(e);
            }
        });
    }

    public interface CompletionSurveyCallback{
        void CompletionSurveySuccess(Survey survey);
        void CompletionSurveyError(Exception e);
    }
}