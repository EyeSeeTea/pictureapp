package org.eyeseetea.malariacare.data.sync.exporter;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySendAction;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class ConvertToWSVisitor implements IConvertToSDKVisitor {
    private static final String SURVEY_ACTION_ID = "issueReferral";

    private SurveyContainerWSObject mSurveyContainerWSObject;
    private String language;

    public ConvertToWSVisitor() {
        ICredentialsRepository credentialsRepository = new CredentialsLocalDataSource();
        ISettingsRepository currentLanguageRepository = new SettingsDataSource();
        IUserRepository userDataSource = new UserAccountDataSource();
        UserAccount userAccount = userDataSource.getLoggedUser();
        Credentials credentials = credentialsRepository.getOrganisationCredentials();
        language = currentLanguageRepository.getSettings().getLanguage();
        mSurveyContainerWSObject = new SurveyContainerWSObject(
                PreferencesState.getInstance().getContext().getString(
                        R.string.ws_version), PreferencesState.getInstance().getContext().getString(
                R.string.ws_source), credentials.getUsername(),
                credentials.getPassword(), language, getAndroidInfo(userAccount), "");
    }

    private String getAndroidInfo(UserAccount userAccount) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(userAccount.getPhone().getIMEI());
        stringBuilder.append(", ");
        stringBuilder.append(userAccount.getPhone().getValue());
        stringBuilder.append(", ");
        stringBuilder.append(userAccount.getAppVersion());

        return stringBuilder.toString();
    }

    private static List<AttributeValueWS> getValuesWSFromSurvey(SurveyDB survey) {
        List<AttributeValueWS> valueWSes = new ArrayList<>();
        for (ValueDB value : survey.getValuesFromDB()) {
            if (value.getQuestionDB() != null) {
                if (value.getOptionDB() == null) {
                    valueWSes.add(
                            new AttributeValueWS(value.getQuestionDB().getCode(),
                                    value.getValue()));
                } else {
                    valueWSes.add(new AttributeValueWS(value.getQuestionDB().getCode(),
                            value.getOptionDB().getCode()));
                }

            }
        }
        return valueWSes;
    }

    @Override
    public void visit(SurveyDB survey) throws ConversionException {
        SurveySendAction surveySendAction = new SurveySendAction();
        surveySendAction.setActionId(CodeGenerator.generateCode());
        surveySendAction.setType(SURVEY_ACTION_ID);
        surveySendAction.setAttributeValues(getValuesWSFromSurvey(survey));
        mSurveyContainerWSObject.getActions().add(surveySendAction);
    }

    public SurveyContainerWSObject getSurveyContainerWSObject() {
        return mSurveyContainerWSObject;
    }
}
