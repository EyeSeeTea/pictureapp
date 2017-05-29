package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.exception.ConversionException;
import org.eyeseetea.malariacare.domain.exception.InvalidCredentialsException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WSClient {
    private static final String TAG = "WSClient";

    private Retrofit mRetrofit;
    private Context mContext;
    private SurveyApiClientRetrofit mSurveyApiClientRetrofit;

    public WSClient() throws IllegalArgumentException{
        mContext = PreferencesState.getInstance().getContext();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        mRetrofit = new Retrofit.Builder()
                .baseUrl(PreferencesEReferral.getWSURL())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        mSurveyApiClientRetrofit = mRetrofit.create(SurveyApiClientRetrofit.class);

    }

    public void pushSurveys(SurveyContainerWSObject surveyContainerWSObject,
            WSClientCallBack wsClientCallBack) {

        Response<SurveyWSResult> response = null;

        try {
            response = mSurveyApiClientRetrofit.pushSurveys(
                    surveyContainerWSObject).execute();
        } catch (UnrecognizedPropertyException e) {
            ConversionException conversionException = new ConversionException(e);
            wsClientCallBack.onError(conversionException);
        } catch (IOException e) {
            wsClientCallBack.onError(e);
        }
        if (response != null && response.code() == 401) {
            ConversionException conversionException = new ConversionException(null);
            wsClientCallBack.onError(conversionException);
        } else if (response != null && response.code() == 402) {
            InvalidCredentialsException invalidCredentialsException =
                    new InvalidCredentialsException();
            wsClientCallBack.onError(invalidCredentialsException);
        } else if (response != null && response.isSuccessful()) {
            wsClientCallBack.onSuccess(response.body());
        } else {
            Log.e(TAG, "Failed response when pushing surveys");
            wsClientCallBack.onError(new ConversionException(null));
        }

    }

    public interface WSClientCallBack {
        void onSuccess(SurveyWSResult surveyWSResult);

        void onError(Exception e);
    }

}
