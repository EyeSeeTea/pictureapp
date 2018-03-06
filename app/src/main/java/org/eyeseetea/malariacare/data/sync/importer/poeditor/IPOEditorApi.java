package org.eyeseetea.malariacare.data.sync.importer.poeditor;


import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.LanguagesResult;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.POEditorResponse;
import org.eyeseetea.malariacare.data.sync.importer.poeditor.models.TermsResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IPOEditorApi {

    @FormUrlEncoded
    @POST("v2/languages/list")
    Call<POEditorResponse<LanguagesResult>> getLanguages(@Field("api_token") String token
            , @Field("id") String id);

    @FormUrlEncoded
    @POST("v2/terms/list")
    Call<POEditorResponse<TermsResult>> getTranslations(@Field("api_token") String token
            , @Field("id") String id, @Field("language") String language);
}
