package org.eyeseetea.malariacare.data.sync.importer;

import static org.eyeseetea.malariacare.domain.usecase.pull.PullStep.BUILDING_SURVEYS;
import static org.eyeseetea.malariacare.domain.usecase.pull.PullStep.BUILDING_VALUES;

import android.content.Context;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.sync.importer.strategies.DataConverterStrategy;
import org.eyeseetea.malariacare.data.sync.importer.strategies.IDataConverterStrategy;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.exception.QuestionNotFoundException;

import java.util.List;

public class DataConverter {

    IDataConverterStrategy dataConverterStrategy;

    public DataConverter(Context context) {
        dataConverterStrategy = new DataConverterStrategy(context);
    }

    public void convert(IPullController.Callback callback, ConvertFromSDKVisitor converter) {
        String orgUnitName = PreferencesState.getInstance().getOrgUnit();

        List<OrgUnit> orgUnits = converter.getOrgUnits();

        for (OrgUnit orgUnit : orgUnits) {

            //Only events for the right ORGUNIT are loaded
            if (!orgUnitName.isEmpty() &&
                    orgUnit.getName() != null && !orgUnit.getName().equals(
                    orgUnitName)) {
                continue;
            }

            List<Program> programs = Program.getAllPrograms();

            for (Program program : programs) {
                List<EventExtended> events = EventExtended.getExtendedList(
                        SdkQueries.getEvents(orgUnit.getUid(), program.getUid()));

                callback.onStep(BUILDING_SURVEYS);

                for (EventExtended event : events) {
                    event.accept(converter);
                }

                callback.onStep(BUILDING_VALUES);

                for (EventExtended event : events) {

                    List<DataValueExtended> dataValues = DataValueExtended.getExtendedList(
                            SdkQueries.getDataValues(event.getUid()));

                    for (DataValueExtended dataValueExtended : dataValues) {
                        dataValueExtended.accept(converter);
                    }

                    try {
                        dataConverterStrategy.convert(converter, event);
                    } catch (QuestionNotFoundException e) {
                        callback.onError(e);
                    }
                }
            }
        }

        saveConvertedSurveys(callback, converter);

    }

    private static void saveConvertedSurveys(final IPullController.Callback callback,
            final ConvertFromSDKVisitor converter) {

        List<Survey> surveys = converter.getSurveys();

        Survey.saveAll(surveys, new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveConvertedValues(callback, converter);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(new PullConversionException(throwable));
            }
        });
    }

    private static void saveConvertedValues(final IPullController.Callback callback,
            ConvertFromSDKVisitor converter) {

        List<Value> values = converter.getValues();

        Value.saveAll(values, new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(new PullConversionException(throwable));
            }
        });
    }
}
