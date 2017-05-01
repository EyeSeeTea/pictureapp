package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.usecase.DateFilter;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.eyeseetea.malariacare.layout.adapters.general.StringArrayAdapter;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashboardSentFilter {

    //surveysList contains the list of all surveys without filters
    List<Survey> surveysList;
    Spinner filterSpinnerSurveys;
    String lastFilterSelection;
    DateFilter surveyFilter;
    CustomTextView surveyCases;
    DashboardSentFragment dashboardSentFragment;
    /*
    ** Flag to prevents the false click on filter creation.
     */
    boolean initiatingFilters = true;

    public void initFilters(DashboardSentFragment dashboardSentFragment, ListView listView, List<Survey> surveyList) {
        this.surveysList = surveyList;
        this.dashboardSentFragment = dashboardSentFragment;
        surveyFilter = new DateFilter();
        surveyCases = (CustomTextView) listView.findViewById(R.id.survey_cases);
        initOrgUnitFilters(listView);
        reloadSentSurveys();
    }

    private void initOrgUnitFilters(ListView view) {
        initiatingFilters = true;
        filterSpinnerSurveys = (Spinner) view.findViewById(R.id.filter_surveys);
        ArrayList<String> filterList = initFilterList();
        filterSpinnerSurveys.setAdapter(new StringArrayAdapter(view.getContext(),
                filterList));
        if (lastFilterSelection == null) {
            lastFilterSelection = filterList.get(0);
        }
        filterSpinnerSurveys.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                String selectedOption = parent.getItemAtPosition(position).toString();
                if (!selectedOption.equals(lastFilterSelection)) {
                    lastFilterSelection = selectedOption;
                    reloadSurveyFilter();
                    reloadSentSurveys();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        reloadSurveyFilter();
        reloadSentSurveys();
    }

    private void reloadSurveyFilter() {
        Context context = PreferencesState.getInstance().getContext();
        surveyFilter = new DateFilter();
        if(lastFilterSelection.equals(context.getString(R.string.last_6_days))){
            surveyFilter.setLast6Days(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.last_6_weeks))) {
            surveyFilter.setLast6Weeks(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.last_6_months))) {
            surveyFilter.setLast6Month(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.unsent_survey_filter_option_last_week))) {
            surveyFilter.setLastWeek(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.unsent_survey_filter_option_last_month))) {
            surveyFilter.setLastMonth(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.unsent_survey_filter_option_this_week))) {
            surveyFilter.setThisWeek(true);
        }else if(lastFilterSelection.equals(context.getString(R.string.unsent_survey_filter_option_this_month))) {
            surveyFilter.setThisMonth(true);
        }
    }

    private void reloadSentSurveys() {
        List <Survey> filteredSurveyList = filterSurveys();
        dashboardSentFragment.reloadSurveys(filteredSurveyList);
        surveyCases.setText(String.format(PreferencesState.getInstance().getContext().getString(R.string.unsent_survey_filter_number_of_cases), filteredSurveyList.size()));
    }

    private List<Survey> filterSurveys() {
        List<Survey> surveysFilteredList = new ArrayList<>();
        if(surveyFilter.isAll()) {
            surveysFilteredList = surveysList;
        }else {
            Date startDate = surveyFilter.getStartFilterDate(Calendar.getInstance());
            Date endDate = surveyFilter.getEndFilterDate(Calendar.getInstance());
            for(Survey survey: surveysList){
                if(surveyFilter.isDateBetweenDates(survey.getCompletionDate(), startDate, endDate)){
                    surveysFilteredList.add(survey);
                }
            }
        }
        return surveysFilteredList;
    }

    private ArrayList<String> initFilterList() {
        ArrayList<String> filterList = new ArrayList<>();
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.unsent_survey_filter_option_this_week));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.unsent_survey_filter_option_this_month));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.unsent_survey_filter_option_last_week));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.unsent_survey_filter_option_last_month));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.last_6_days));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.last_6_weeks));
        filterList.add(PreferencesState.getInstance().getContext().getString(
                R.string.last_6_months));
        return filterList;
    }
}
