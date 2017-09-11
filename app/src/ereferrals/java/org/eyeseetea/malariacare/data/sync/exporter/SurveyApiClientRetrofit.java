package org.eyeseetea.malariacare.data.sync.exporter;

import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyContainerWSObject;
import org.eyeseetea.malariacare.data.sync.exporter.model.SurveyWSResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SurveyApiClientRetrofit {
    @POST("eRefWSDev/api")
    Call<SurveyWSResult> pushSurveys(@Body SurveyContainerWSObject surveyContainerWSObject);
}
