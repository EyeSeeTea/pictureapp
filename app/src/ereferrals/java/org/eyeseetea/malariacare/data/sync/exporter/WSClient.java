package org.eyeseetea.malariacare.data.sync.exporter;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;
import org.eyeseetea.malariacare.domain.exception.ConversionException;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class WSClient {
    private static final String TAG = "WSClient";

    private Retrofit mRetrofit;
    private Context mContext;
    private SurveyApiClientRetrofit mSurveyApiClientRetrofit;

    public WSClient() {
        mContext = PreferencesState.getInstance().getContext();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mContext.getString(R.string.ws_base_url))
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        mSurveyApiClientRetrofit = mRetrofit.create(SurveyApiClientRetrofit.class);

    }

    public void pushSurveys(SurveyContainerWSObject surveyContainerWSObject,
            WSClientCallBack wsClientCallBack) {

        Response<SurveyWSResult> response = null;
        try {
            response = mSurveyApiClientRetrofit.pushSurveys(
                    surveyContainerWSObject).execute();
        } catch (IOException e) {
            wsClientCallBack.onError(e);
        }
        if (response != null && response.isSuccessful()) {
            wsClientCallBack.onSuccess(response.body());
        } else {
            Log.e(TAG, "Failed response when pushing surveys");
            wsClientCallBack.onError(new ConversionException());
        }
    }

    public interface WSClientCallBack {
        void onSuccess(SurveyWSResult surveyWSResult);

        void onError(Exception e);
    }

}
