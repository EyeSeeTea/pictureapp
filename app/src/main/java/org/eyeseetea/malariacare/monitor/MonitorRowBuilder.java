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
     * Css to style the first column of the table
     */
    protected static final String CSS_ROW_METRIC="rowMetric";
    /**
     * Css to style a column that refers to a time period
     */
    protected static final String CSS_ROW_TIMEUNIT="rowTimeUnit";
    /**
     * Css to style a column with a plain value
     */
    protected static final String CSS_ROW_VALUE="rowValue";
    /**
     * Css to style a column with summary info
     */
    protected static final String CSS_ROW_SUMMARY="rowMetric rowSummary";

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
    protected Object[] monthsData;

    /**
     * Data (raw value) for each column considering weeks as timeunit
     */
    protected Object[] weeksData;

    /**
     * Data (raw value) for each column considering days as timeunit
     */
    protected Object[] daysData;

    /**
     * Context required to translate strings (if needed)
     */
    protected Context context;

    public MonitorRowBuilder(Context context, String rowTitle){
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
     * Returns the title of the row
     * @return
     */
    public String getRowTitle(){
        return this.rowTitle;
    }

    /**
     * Default value for each column
     * @return
     */
    protected Object defaultValueColumn(){
        return 0;
    }

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

        String ROW_JSON="{\"columnClasses\":[%s],\"columnData\":{\"months\":[%s],\"weeks\":[%s],\"days\":[%s]}}";
        String rowJSON=String.format(ROW_JSON,
                getColumnClassesAsJSON(),
                getDataAsJSON(monthsData),
                getDataAsJSON(weeksData),
                getDataAsJSON(daysData)
        );
        return rowJSON;
    }

    /**
     * Turns the list of column classes into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     * @return
     */
    private String getColumnClassesAsJSON(){
        return convertListStringToJSON(columnClasses);
    }

    /**
     * Turns an array of objects into a list of quoted items (via toString()) adding rowTitle as first item
     * @param data
     * @return
     */
    private String getDataAsJSON(Object[] data){
        List<String> dataAsString=new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        dataAsString.add(this.rowTitle);
        for(int i=0;i<data.length;i++){
            dataAsString.add(data[i].toString());
        }
        return convertListStringToJSON(dataAsString);
    }

    /**
     * Turns the list of strings into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     * @return
     */
    private String convertListStringToJSON(List<String> valuesAsString){
        int numValues=valuesAsString.size();
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<numValues;i++){
            stringBuilder.append("\""+valuesAsString.get(i)+"\"");
            if(i!=(numValues-1)){
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
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
        this.weeksData[columnIndex]=updateColumn(this.weeksData[columnIndex],survey);
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
        this.daysData[columnIndex]=updateColumn(this.daysData[columnIndex],survey);
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

}
