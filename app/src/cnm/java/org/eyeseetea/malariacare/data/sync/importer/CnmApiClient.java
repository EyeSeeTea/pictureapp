package org.eyeseetea.malariacare.data.sync.importer;

import android.util.Log;

import org.eyeseetea.malariacare.data.authentication.api.AuthenticationApiStrategy;
import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;
import org.eyeseetea.malariacare.data.sync.importer.models.Version;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.LanguagesDownloadException;

import java.io.IOException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class CnmApiClient {
    public static final String ADMIN_NAMESPACE = "admin";
    public static final String HIERARCHY_KEY = "hierarchy";
    public static final String VERSION_KEY = "version";
    private static final java.lang.String TAG = "CnmApiClient";
    public String mBaseAddress;
    private Retrofit mRetrofit;
    private OrganisationUnitTreeApiClient mOrganisationUnitTreeApiClient;


    public CnmApiClient(String baseAddress)
            throws ApiCallException, ConfigJsonIOException, LanguagesDownloadException {
        mBaseAddress = baseAddress;


        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(
                new BasicAuthInterceptor(AuthenticationApiStrategy.getApiCredentials())).build();

        mRetrofit = new Retrofit.Builder()
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .baseUrl(baseAddress)
                .build();

        mOrganisationUnitTreeApiClient = mRetrofit.create(OrganisationUnitTreeApiClient.class);
    }

    public void getOrganisationUnitTree(
            CnmApiClientCallBack<List<OrgUnitTree>> cnmApiClientCallBack) {
        getOrganisationUnitTree(ADMIN_NAMESPACE, HIERARCHY_KEY, cnmApiClientCallBack);
    }

    public void getOrganisationUnitTree(String namespace, String key,
            final CnmApiClientCallBack<List<OrgUnitTree>> cnmApiClientCallBack) {
        try {
            Response<List<OrgUnitTree>> response = mOrganisationUnitTreeApiClient.getOrgUnitsTree(
                    namespace, key).execute();
            if (response != null && response.isSuccessful()) {
                cnmApiClientCallBack.onSuccess(response.body());
            } else {
                Log.e(TAG, "Failed response getting TreeOrgUnits" + response != null
                                    ? response.raw().toString() : "");
                            cnmApiClientCallBack.onError(
                                    new Exception("Failed response getting TreeOrgUnits"));
            }

        } catch (IOException e) {
            e.printStackTrace();
            cnmApiClientCallBack.onError(new ApiCallException(e));
        }
    }

    public void getMetadataVersion(CnmApiClientCallBack<Version> cnmApiClientCallBack) {
        getMetadataVersion(ADMIN_NAMESPACE, VERSION_KEY, cnmApiClientCallBack);
    }

    public void getMetadataVersion(String namespace, String key,
            final CnmApiClientCallBack<Version> cnmApiClientCallBack) {
        try {
            Response<Version> response = mOrganisationUnitTreeApiClient.getMetadataVersion(
                    namespace,
                    key).execute();
            if (response != null && response.isSuccessful()) {
                cnmApiClientCallBack.onSuccess(response.body());
            } else {
                Log.e(TAG, "Failed response getting Version" + response != null
                        ? response.raw().toString() : "");
                cnmApiClientCallBack.onError(
                        new Exception("Failed response getting Version"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            cnmApiClientCallBack.onError(new ApiCallException(e));
        }
    }

    public interface CnmApiClientCallBack<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String credentials) {
            this.credentials = credentials;
        }

        @Override
        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }
}
