package org.eyeseetea.malariacare.data.sync.exporter;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySendAction;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;

import java.util.ArrayList;
import java.util.List;

public class ConvertToWSVisitor implements IConvertToSDKVisitor {
    private static final String SURVEY_ACTION_ID = "issueReferral";

    private SurveyContainerWSObject mSurveyContainerWSObject;

    public ConvertToWSVisitor() {
        ICredentialsRepository credentialsRepository = new CredentialsLocalDataSource();
        Credentials credentials = credentialsRepository.getOrganisationCredentials();
        mSurveyContainerWSObject = new SurveyContainerWSObject(
                PreferencesState.getInstance().getContext().getString(
                        R.string.ws_version), PreferencesState.getInstance().getContext().getString(
                R.string.ws_source), credentials.getUsername(),
                credentials.getPassword());
    }

    private static List<AttributeValueWS> getValuesWSFromSurvey(Survey survey) {
        List<AttributeValueWS> valueWSes = new ArrayList<>();
        for (Value value : survey.getValuesFromDB()) {
            if (value.getQuestion() != null) {
                if (value.getOption() == null) {
                    valueWSes.add(
                            new AttributeValueWS(value.getQuestion().getCode(),
                                    value.getValue()));
                } else {
                    valueWSes.add(new AttributeValueWS(value.getQuestion().getCode(),
                            value.getOption().getCode()));
                }

            }
        }
        return valueWSes;
    }

    @Override
    public void visit(Survey survey) throws ConversionException {
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
