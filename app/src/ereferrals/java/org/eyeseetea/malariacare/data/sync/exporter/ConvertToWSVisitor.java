package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.location.Location;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.AttributeValueWS;
import org.eyeseetea.malariacare.data.sync.exporter.model.Coordinate;
import org.eyeseetea.malariacare.data.sync.exporter.model.SettingsSummary;
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
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ConvertToWSVisitor implements IConvertToSDKVisitor {
    private static final String SURVEY_ACTION_ID = "issueReferral";

    private SurveyContainerWSObject mSurveyContainerWSObject;
    private IDeviceRepository deviceRepository;
    ICredentialsRepository credentialsRepository;
    ISettingsRepository settingsRepository;
    IAppInfoRepository appInfoRepository;
    IProgramRepository programRepository;
    ICountryVersionRepository countryVersionRepository;

    public ConvertToWSVisitor(IDeviceRepository deviceRepository,
                              ICredentialsRepository credentialsRepository,
                              ISettingsRepository settingsRepository,
                              IAppInfoRepository appInfoRepository,
                              IProgramRepository programRepository,
                              ICountryVersionRepository countryVersionRepository) {
        this.deviceRepository = deviceRepository;
        this.credentialsRepository = credentialsRepository;
        this.settingsRepository  = settingsRepository;
        this.appInfoRepository = appInfoRepository;
        this.programRepository = programRepository;;
        this.countryVersionRepository = countryVersionRepository;
        init();
    }

    private void init() {
        Device device = deviceRepository.getDevice();
        AppInfo appInfo = appInfoRepository.getAppInfo();
        Credentials credentials = credentialsRepository.getLastValidCredentials();
        Settings settings = settingsRepository.getSettings();
        mSurveyContainerWSObject = new SurveyContainerWSObject(
                settings.getWsVersion(), device.getAndroidVersion(), credentials.getUsername(),
                credentials.getPassword(), device.getIMEI(),
                getConfigFileVersion(), appInfo.getAppVersion(),
                getDateString(appInfo.getUpdateMetadataDate()),
                getSettingsSummary(settings), device.getPhone(), device.getIMEI());
    }

    private String getDateString(Date updateMetadataDate) {
        if(updateMetadataDate==null){
            return null;
        }
        return Utils.parseDateToString(updateMetadataDate, "yyyy-MM-dd hh:mm");
    }

    private SettingsSummary getSettingsSummary(Settings settings) {
        return new SettingsSummary(settings.getWsServerUrl(),
                settings.getWebUrl(), settings.isCanDownloadWith3G(), settings.getFontSize(),
                settings.isElementActive(), settings.getLanguage());
    }

    private int getConfigFileVersion() {
        String uid = programRepository.getUserProgram().getId();
        Configuration.CountryVersion countryVersion =
                countryVersionRepository.getCountryVersionForUID(uid);
        if (countryVersion != null) {
            return countryVersion.getVersion();
        } else {
            return 0;
        }
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
        surveySendAction.setActionId(survey.getEventUid());
        surveySendAction.setType(SURVEY_ACTION_ID);
        surveySendAction.setDataValues(getValuesWSFromSurvey(survey));
        surveySendAction.setVoucher(new Voucher(survey.getVoucherUid(), getVoucherType(survey)));
        surveySendAction.setProgram(programRepository.getUserProgram().getCode());
        surveySendAction.setSourceAddedDateTime(getEventDateTimeString(survey.getEventDate()));
        Location location = LocationMemory.get(survey.getId_survey());
        surveySendAction.setCoordinate(
                new Coordinate(location.getLatitude(), location.getLongitude()));
        surveySendAction.setAccuracy(location.getAccuracy());
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
