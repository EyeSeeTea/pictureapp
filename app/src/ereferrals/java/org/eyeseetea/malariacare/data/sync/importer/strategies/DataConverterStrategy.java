package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.domain.exception.QuestionNotFoundException;

import java.util.List;

public class DataConverterStrategy implements IDataConverterStrategy {
    private static String TAG = ".DataConverterStrategy";

    private static String ORG_UNIT_QUESTION_UID = "NoDataElementOrgUnit";

    private static String SEX_PREGNANCY_QUESTION_UID = "NoDataElementSexPregnancy";

    private static String SEX_PREGNANCY_MALE_VALUE = "NoDataElementM";
    private static String SEX_PREGNANCY_FEMALE_VALUE = "NoDataElementF";
    private static String SEX_PREGNANCY_PREGNANT_VALUE = "NoDataElementFP";

    private static String SEX_QUESTION_UID = "fculIlFe15p";
    private static String PREGNANT_QUESTION_UID = "fxDu5J5eZ4t";

    Context mContext;

    public DataConverterStrategy(Context context) {
        mContext = context;
    }


    public void convert(ConvertFromSDKVisitor converter,
            EventExtended event) throws QuestionNotFoundException {

        convertOrgUnitDataValue(converter, event);

        convertPatientSexPregnancyDataValue(converter, event);
    }

    private void convertOrgUnitDataValue(ConvertFromSDKVisitor converter,
            EventExtended event) throws QuestionNotFoundException {

        Question orgUnitQuestion = Question.findByUID(ORG_UNIT_QUESTION_UID);

        if (orgUnitQuestion == null) {
            throw new QuestionNotFoundException(
                    String.format("Question with uid %s not found", ORG_UNIT_QUESTION_UID));
        }

        DataValueExtended OrgUnitDataValue = new DataValueExtended();
        OrgUnitDataValue.setEvent(event.getEvent());
        OrgUnitDataValue.setDataElement(orgUnitQuestion.getUid());
        OrgUnitDataValue.setValue(event.getOrganisationUnitId());
        OrgUnitDataValue.accept(converter);

    }

    private void convertPatientSexPregnancyDataValue(ConvertFromSDKVisitor converter,
            EventExtended event) throws QuestionNotFoundException {

        Question sexPregnancyQuestion = Question.findByUID(SEX_PREGNANCY_QUESTION_UID);

        if (sexPregnancyQuestion == null) {
            Log.d(TAG, event.getUid() + "With invalid sexPregnancy question");
            return;
        }

        List<DataValueExtended> dataValues = DataValueExtended.getExtendedList(
                SdkQueries.getDataValues(event.getUid()));

        DataValueExtended sexDataValue = getDataValue(dataValues, SEX_QUESTION_UID);
        DataValueExtended pregnancyDataValue = getDataValue(dataValues, PREGNANT_QUESTION_UID);

        if (sexDataValue == null || pregnancyDataValue == null) {
            Log.d(TAG, event.getUid() + "With invalid sexPregnancy question");
            return;
        }

        DataValueExtended OrgUnitDataValue = new DataValueExtended();
        OrgUnitDataValue.setEvent(event.getEvent());
        OrgUnitDataValue.setDataElement(sexPregnancyQuestion.getUid());
        if (sexDataValue.getValue().equals("F")) {
            if (pregnancyDataValue.getValue().equals("true")) {
                OrgUnitDataValue.setValue(SEX_PREGNANCY_PREGNANT_VALUE);
            } else {
                OrgUnitDataValue.setValue(SEX_PREGNANCY_FEMALE_VALUE);
            }
        } else {
            OrgUnitDataValue.setValue(SEX_PREGNANCY_MALE_VALUE);
        }


        OrgUnitDataValue.accept(converter);

    }

    private static DataValueExtended getDataValue(List<DataValueExtended> dataValues, String uid) {
        DataValueExtended dataValuesExtended = null;

        for (DataValueExtended dataValue : dataValues) {
            if (dataValue.getDataElement().equals(uid)) {
                dataValuesExtended = dataValue;
            }
        }

        return dataValuesExtended;
    }
}
