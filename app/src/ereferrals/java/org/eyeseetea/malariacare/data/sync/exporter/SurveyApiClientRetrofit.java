package org.eyeseetea.malariacare.data.sync.exporter;

import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SurveyApiClientRetrofit {
    @POST("eRefWSTrain/api")
    Call<SurveyWSResult> pushSurveys(@Body SurveyContainerWSObject surveyContainerWSObject);

    @POST("eRefWSTrain/api/forgotpassword")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordPayload forgotPasswordPayload);
}
