package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.model.AuthPayload;
import org.eyeseetea.malariacare.data.remote.model.AuthResponse;
import org.eyeseetea.malariacare.data.sync.exporter.model.ApiAvailable;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.data.sync.exporter.model.Id;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySimpleObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySimpleWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySimpleWSResponseObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.AvailableApiException;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class eReferralsAPIClient {
    private static final java.lang.String TAG = "eReferralsAPIClient";

    private Retrofit mRetrofit;
    private Context mContext;
    private ApiClientRetrofit mApiClientRetrofit;
    private OkHttpClient mOkHttpClient;
    public String mBaseAddress;
    private final int DEFAULT_TIMEOUT = 50000;

    public eReferralsAPIClient(String baseAddress) throws IllegalArgumentException {
        if(baseAddress.equals(Credentials.createDemoCredentials().getServerURL())){
            return;
        }
        mBaseAddress = baseAddress;
        mContext = PreferencesState.getInstance().getContext();

        initializeDependencies(DEFAULT_TIMEOUT);
    }

    private void initializeDependencies(int timeoutMillis) {
        timeoutMillis += DEFAULT_TIMEOUT;

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        mOkHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMillis, TimeUnit.MILLISECONDS)
                .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseAddress)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .client(mOkHttpClient)
                .build();

        mApiClientRetrofit = mRetrofit.create(ApiClientRetrofit.class);
    }

    public AuthResponse auth(String userCode, String pin) throws IOException,
            AvailableApiException {
        AuthPayload authPayload = new AuthPayload(userCode, pin);

        getIfIsApiAvailable();

        Response<AuthResponse> authResponse = mApiClientRetrofit.auth(authPayload).execute();

        return authResponse.body();
    }

    public void setTimeoutMillis(int timeoutMillis) {
        initializeDependencies(timeoutMillis);
    }

    public void pushSurveys(SurveyContainerWSObject surveyContainerWSObject,
            WSClientCallBack wsClientCallBack) {

        Response<SurveyWSResult> response = null;

        try {
            getIfIsApiAvailable();
            response = mApiClientRetrofit.pushSurveys(
                    surveyContainerWSObject).execute();
        } catch (UnrecognizedPropertyException e) {
            ConversionException conversionException = new ConversionException(e);
            wsClientCallBack.onError(conversionException);
        } catch (SocketTimeoutException | UnknownHostException e) {
            wsClientCallBack.onError(new NetworkException());
            return;
        } catch (AvailableApiException e) {
            wsClientCallBack.onError(e);
            return;
        } catch (IOException e) {
            wsClientCallBack.onError(e);
        }
        if (response != null && response.code() == 401) {
            ConversionException conversionException = new ConversionException(null);
            wsClientCallBack.onError(conversionException);
        } else if (response != null && response.code() == 402) {
            //This exception is created when the the hardcoded credentials was invalid.
            InvalidCredentialsException invalidCredentialsException =
                    new InvalidCredentialsException();
            wsClientCallBack.onError(invalidCredentialsException);
        } else if (response != null && response.code() == 209) {
            ConfigFileObsoleteException configFileObsoleteException =
                    new ConfigFileObsoleteException();
            wsClientCallBack.onSuccess(response.body());
            wsClientCallBack.onError(configFileObsoleteException);
        } else if (response != null && response.isSuccessful()) {
            wsClientCallBack.onSuccess(response.body());
        } else {
            Log.e(TAG, "Failed response when pushing surveys");
            wsClientCallBack.onError(new ConversionException(null));
        }

    }

    public void getForgotPassword(ForgotPasswordPayload forgotPasswordPayload,
            WSClientCallBack<ForgotPasswordResponse> wsClientCallBack) {
        Response<ForgotPasswordResponse> response = null;
        try {
            response = mApiClientRetrofit.forgotPassword(forgotPasswordPayload).execute();

        } catch (UnrecognizedPropertyException e) {
            ConversionException conversionException = new ConversionException(e);
            wsClientCallBack.onError(conversionException);
        } catch (IOException e) {
            wsClientCallBack.onError(e);
        }
        if (response != null && response.isSuccessful()) {
            wsClientCallBack.onSuccess(response.body());
        } else {
            wsClientCallBack.onError(
                    new ApiCallException(mContext.getString(R.string.ws_unknown_exception)));
        }
    }


    public void getExistOnServer(List<Survey> surveyList, WSClientCallBack wsClientCallBack){
        SurveySimpleWSObject surveySimpleWSObject = new SurveySimpleWSObject();
        ArrayList<Id> ids = new ArrayList<>();
        ArrayList<String> existOnServer = new ArrayList<>();
        if(surveyList.size()>0) {
            for (Survey survey : surveyList) {
                ids.add(new Id(survey.getUId()));
            }
            surveySimpleWSObject.setActions(ids);
            try {
                Response<SurveySimpleWSResponseObject> response =
                        mApiClientRetrofit.getQuarantineSurveys(
                                surveySimpleWSObject).execute();
                for (SurveySimpleObject surveySimpleObject : response.body().getActions()) {
                    for (Survey survey : surveyList) {
                        if (surveySimpleObject.getId().equals(survey.getUId())) {
                            if (surveySimpleObject.isExistOnServer()) {
                                existOnServer.add(survey.getUId());
                            }
                        }
                    }
                }
            } catch (UnrecognizedPropertyException e) {
                ConversionException conversionException = new ConversionException(e);
                wsClientCallBack.onError(conversionException);
            } catch (SocketTimeoutException | UnknownHostException e) {
                wsClientCallBack.onError(new NetworkException());
                return;
            } catch (IOException e) {
                wsClientCallBack.onError(e);
            }
        }
        wsClientCallBack.onSuccess(existOnServer);
    }

    public ApiAvailable getIfIsApiAvailable()
            throws IOException, AvailableApiException {
        Response<ApiAvailable> response = mApiClientRetrofit.apiAvailable().execute();
        if (!response.isSuccessful()) {
            throw new AvailableApiException();
        } else if (!response.body().isAvailable()) {
            throw new AvailableApiException(response.body().getMsg());
        }
        return response.body();
    }

    public interface WSClientCallBack<T> {
        void onSuccess(T result);

        void onError(Exception e);
    }
}
