package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.location.Location;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.CountryVersionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.Coordinate;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySendAction;
import org.eyeseetea.malariacare.data.sync.exporter.model.Voucher;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICountryVersionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Device;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.client.sdk.core.common.utils.CodeGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ConvertToWSVisitor implements IConvertToSDKVisitor {
    private static final String SURVEY_ACTION_ID = "issueReferral";

    private SurveyContainerWSObject mSurveyContainerWSObject;
    private String language;

    public ConvertToWSVisitor() {
        IDeviceRepository deviceDataSource = new DeviceDataSource();
        Device device = deviceDataSource.getDevice();
        init(device);
    }

    public ConvertToWSVisitor(Device device) {
        init(device);
    }

    private void init(Device device) {
        ICredentialsRepository credentialsRepository = new CredentialsLocalDataSource();
        ISettingsRepository currentLanguageRepository = new SettingsDataSource();
        IAppInfoRepository appInfoDataSource = new AppInfoDataSource();
        AppInfo appInfo = appInfoDataSource.getAppInfo();
        Credentials credentials = credentialsRepository.getOrganisationCredentials();
        language = currentLanguageRepository.getSettings().getLanguage();
        mSurveyContainerWSObject = new SurveyContainerWSObject(
                PreferencesState.getInstance().getContext().getString(
                        R.string.ws_version), device.getAndroidVersion(), credentials.getUsername(),
                credentials.getPassword(), language, getAndroidInfo(device, appInfo),
                appInfo.getMetadataVersion(), getConfigFileVersion());
    }

    private int getConfigFileVersion() {
        IProgramRepository mProgramLocalDataSource = new ProgramLocalDataSource();
        String uid = mProgramLocalDataSource.getUserProgram().getId();
        ICountryVersionRepository countryVersionRepository = new CountryVersionLocalDataSource();
        Configuration.CountryVersion countryVersion =
                countryVersionRepository.getCountryVersionForUID(uid);
        if (countryVersion != null) {
            return countryVersion.getVersion();
        } else {
            return 0;
        }
    }

    private String getAndroidInfo(Device device, AppInfo appInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        if(device.getIMEI()!=null) {
            stringBuilder.append(device.getIMEI());
            stringBuilder.append(", ");
        }
        if(device.getPhone()!=null && !device.getPhone().isEmpty()) {
            stringBuilder.append(device.getPhone());
            stringBuilder.append(", ");
        }
        stringBuilder.append(appInfo.getAppVersion());

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
        surveySendAction.setDataValues(getValuesWSFromSurvey(survey));
        surveySendAction.setVoucher(new Voucher(survey.getEventUid(), getVoucherType(survey)));
        ProgramLocalDataSource programLocalDataSource = new ProgramLocalDataSource();
        surveySendAction.setProgram(programLocalDataSource.getUserProgram().getCode());
        surveySendAction.setSourceAddedDateTime(getEventDateTimeString(survey.getEventDate()));
        Location location = LocationMemory.get(survey.getId_survey());
        surveySendAction.setCoordinate(
                new Coordinate(location.getLatitude(), location.getLongitude()));
        mSurveyContainerWSObject.getActions().add(surveySendAction);
    }

    private String getEventDateTimeString(Date eventDate) {
        String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        return Utils.parseDateToString(eventDate, ISO_FORMAT, timeZone);
    }

    private String getVoucherType(SurveyDB survey) {
        Context context = PreferencesState.getInstance().getContext();
        if (noIssueVoucher(survey, context)) {
            return Voucher.TYPE_NO_VOUCHER;
        } else if (hasPhone(survey, context)) {
            return Voucher.TYPE_PHONE;
        } else {
            return Voucher.TYPE_PAPER;
        }
    }

    private boolean noIssueVoucher(SurveyDB survey, Context context) {
        OptionDB noIssueOption = survey.getOptionSelectedForQuestionCode(
                context.getString(R.string.issue_voucher_qc));
        if (noIssueOption == null) {
            return false;
        }
        return noIssueOption.getName().equals(
                context.getString(R.string.no_voucher_on));
    }

    private boolean hasPhone(SurveyDB survey, Context context) {
        OptionDB optionDB = survey.getOptionSelectedForQuestionCode(
                context.getString(R.string.phone_ownership_qc));
        if (optionDB == null) {
            return false;
        }
        return !(optionDB.getName().equals(
                context.getString(R.string.no_phone_on)));
    }

    public SurveyContainerWSObject getSurveyContainerWSObject() {
        return mSurveyContainerWSObject;
    }
}
