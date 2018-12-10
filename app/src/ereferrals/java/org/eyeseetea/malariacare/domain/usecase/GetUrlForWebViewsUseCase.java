package org.eyeseetea.malariacare.domain.usecase;

import static org.eyeseetea.malariacare.utils.Utils.convertFromInputStreamToString;
import static org.eyeseetea.malariacare.utils.Utils.getUserLanguageOrDefault;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class GetUrlForWebViewsUseCase implements UseCase {

    public static final int OPEN_TYPE = 0;
    public static final int CLOSED_TYPE = 1;
    public static final int STATUS_TYPE = 2;

    private Context mContext;
    private ICredentialsRepository mCredentialsLocalDataSource;
    private Credentials mCredentials;
    private Callback mCallback;
    private int mUrlType;

    public GetUrlForWebViewsUseCase(Context context,
            ICredentialsRepository credentialsLocalDataSource) {
        mContext = context;
        mCredentialsLocalDataSource = credentialsLocalDataSource;
        initCredentials();
    }

    private void initCredentials() {
        mCredentials = mCredentialsLocalDataSource.getLastValidCredentials();
    }

    public void execute(int urlType, Callback callback) {
        mCallback = callback;
        Credentials credentials = PreferencesState.getCredentialsFromPreferences();
        if (credentials != null && credentials.isDemoCredentials()) {
            mCallback.onGetUrl(null);
        }
        mUrlType = urlType;
        run();
    }

    @Override
    public void run() {
        final String language = getUserLanguageOrDefault(mContext);
        final String typeUrl = getTypeUrlText();
        UIThreadExecutor mainExecutor= new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ISettingsRepository settingsDataSource = new SettingsDataSource(mContext);
        GetSettingsUseCase getSettingsUseCase = new GetSettingsUseCase(mainExecutor, asyncExecutor, settingsDataSource);
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                String url = String.format(setting.getWebUrl() + mContext.getString(
                        R.string.composed_web_view_url), typeUrl, mCredentials.getUsername(),
                        mCredentials.getPassword(), language);
                mCallback.onGetUrl(url);
            }
        });
    }

    private String getTypeUrlText() {
        int resource = R.string.url_open_fragment;

        switch (mUrlType) {
            case OPEN_TYPE:
                resource = R.string.url_open_fragment;
                break;
            case CLOSED_TYPE:
                resource = R.string.url_closed_fragment;
                break;
            case STATUS_TYPE:
                resource = R.string.url_status_fragment;
                break;
        }

        return mContext.getString(resource);
    }

    public interface Callback {
        void onGetUrl(String url);
    }
}
