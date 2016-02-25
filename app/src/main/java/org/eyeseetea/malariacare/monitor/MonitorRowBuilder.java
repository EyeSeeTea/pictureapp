package org.eyeseetea.malariacare.monitor;

import android.content.Context;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates and holds info related to a row in the monitor section
 * Created by arrizabalaga on 25/02/16.
 */
public abstract class MonitorRowBuilder {

    /**
     * Title of the row
     */
    private String rowTitle;

    /**
     * List of css classes for each column of the row
     */
    private List<String> columnClasses;

    /**
     * Data (raw value) for each column considering months as timeunit
     */
    private Object[] monthsData;

    /**
     * Data (raw value) for each column considering weeks as timeunit
     */
    private Object[] weeksData;

    /**
     * Data (raw value) for each column considering days as timeunit
     */
    private Object[] daysData;

    /**
     * Context required to translate strings (if needed)
     */
    private Context context;

    MonitorRowBuilder(Context context, String rowTitle){
        this.context = context;
        this.rowTitle = rowTitle;
        this.columnClasses = defineColumnClasses();
        this.monthsData = initData();
        this.weeksData = initData();
        this.daysData = initData();
    }

    /**
     * Defines the css classes for each column of the row
     */
    protected abstract List<String> defineColumnClasses();

    /**
     * Calculates the new value of the column considering given survey + current column value
     * @param currentValue
     * @param survey
     * @return New value for the same column
     */
    protected abstract Object updateColumn(Object currentValue, Survey survey);

    /**
     * Updates row info with the survey
     * @param survey
     */
    public void addSurvey(Survey survey){
        //Null or not sent surveys are not evaluated
        if(survey==null || survey.getEventDate()==null){
            return;
        }
        //Update data for each time dimension
        addSurveyToMonthsData(survey);
        addSurveyToWeeksData(survey);
        addSurveyToDaysData(survey);
    }

    /**
     * Builds a JSON that is inyected via JS into the webview
     * @return
     */
    public String getRowAsJSON(){
        return "";
    }

    /**
     * Updates months data according to given survey
     * @param survey
     */
    private void addSurveyToMonthsData(Survey survey){
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInMonths(survey.getEventDate());
        //This survey is not relevant to the monitor (too old)
        if(columnIndex==TimePeriodCalculator.COLUMN_NOT_FOUND){
            return;
        }

        //Updates column considering current value + survey
        this.monthsData[columnIndex]=updateColumn(this.monthsData[columnIndex],survey);
    }


    /**
     * Updates months data according to given survey
     * @param survey
     */
    private void addSurveyToWeeksData(Survey survey){
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInWeeks(survey.getEventDate());
        //This survey is not relevant to the monitor (too old)
        if(columnIndex==TimePeriodCalculator.COLUMN_NOT_FOUND){
            return;
        }

        //Updates column considering current value + survey
        this.monthsData[columnIndex]=updateColumn(this.monthsData[columnIndex],survey);
    }


    /**
     * Updates months data according to given survey
     * @param survey
     */
    private void addSurveyToDaysData(Survey survey){
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInDays(survey.getEventDate());
        //This survey is not relevant to the monitor (too old)
        if(columnIndex==TimePeriodCalculator.COLUMN_NOT_FOUND){
            return;
        }

        //Updates column considering current value + survey
        this.monthsData[columnIndex]=updateColumn(this.monthsData[columnIndex],survey);
    }


    /**
     * Inits a list of defaults values (most of times 0 but it could be a
     * @return
     */
    private Object[] initData(){
        Object[] data=new Object[Constants.MONITOR_HISTORY_SIZE];
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            data[i]=defaultValueColumn();
        }
        return data;
    }

    /**
     * Default value for each column
     * @return
     */
    protected Object defaultValueColumn(){
        return 0;
    }

}
