package org.eyeseetea.malariacare.data.sync.exporter;

import org.eyeseetea.malariacare.data.remote.model.AuthPayload;
import org.eyeseetea.malariacare.data.remote.model.AuthResponse;
import org.eyeseetea.malariacare.data.sync.exporter.model.ApiAvailable;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordPayload;
import org.eyeseetea.malariacare.data.sync.exporter.model.ForgotPasswordResponse;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySimpleWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveySimpleWSResponseObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiClientRetrofit {

    @POST("api/auth")
    Call<AuthResponse> auth(@Body AuthPayload authPayload);

    @POST("api")
    Call<SurveyWSResult> pushSurveys(@Body SurveyContainerWSObject surveyContainerWSObject);

    @POST("api/searchActions")
    Call<SurveySimpleWSResponseObject> getQuarantineSurveys(@Body SurveySimpleWSObject surveySimpleWSObject);

    @POST("api/forgotpassword")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordPayload forgotPasswordPayload);

    @GET("api/available")
    Call<ApiAvailable> apiAvailable();
}
