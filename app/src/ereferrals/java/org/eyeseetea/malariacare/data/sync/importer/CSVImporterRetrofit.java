package org.eyeseetea.malariacare.data.sync.importer;


import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CSVImporterRetrofit {
    @GET(PopulateDB.VERSIONS_CSV)
    Call<ResponseBody> getVersionCSV();

    @GET(PopulateDB.PROGRAMS_CSV)
    Call<ResponseBody> getProgramsCSV();

    @GET(PopulateDB.TABS_CSV)
    Call<ResponseBody> getTabsCSV();

    @GET(PopulateDB.ANSWERS_CSV)
    Call<ResponseBody> getAnswersCSV();

    @GET(PopulateDB.OPTION_ATTRIBUTES_CSV)
    Call<ResponseBody> getOptionAttributesCSV();

    @GET(PopulateDB.OPTIONS_CSV)
    Call<ResponseBody> getOptionsCSV();

    @GET(PopulateDB.QUESTIONS_CSV)
    Call<ResponseBody> getQuestionsCSV();

    @GET(PopulateDB.QUESTION_RELATIONS_CSV)
    Call<ResponseBody> getQuestionRelationsCSV();

    @GET(PopulateDB.QUESTION_OPTIONS_CSV)
    Call<ResponseBody> getQuestionOptionsCSV();

    @GET(PopulateDB.QUESTION_THRESHOLDS_CSV)
    Call<ResponseBody> getQuestionThresholdsCSV();

    @GET(PopulateDB.HEADERS_CSV)
    Call<ResponseBody> getHeadersCSV();

    @GET(PopulateDB.MATCHES)
    Call<ResponseBody> getMatchesCSV();
}
