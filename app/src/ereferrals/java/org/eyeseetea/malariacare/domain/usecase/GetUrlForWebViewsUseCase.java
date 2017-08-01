package org.eyeseetea.malariacare.domain.usecase;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;

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
        mCredentials = mCredentialsLocalDataSource.getOrganisationCredentials();
    }

    public void execute(int urlType, Callback callback) {
        mCallback = callback;
        mUrlType = urlType;
        run();
    }

    @Override
    public void run() {
        String typeUrl = getTypeUrlText();
        String url = String.format(mContext.getString(R.string.base_web_view_url), typeUrl,
                mCredentials.getUsername(), mCredentials.getPassword());
        mCallback.onGetUrl(url);
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
