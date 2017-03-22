package org.eyeseetea.malariacare.presentation.factory.stock;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.text.WordUtils;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.rows.StatusRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manuel on 26/12/16.
 */

public abstract class StockRowBuilder {
    public static final String ROW_JSON =
            "{\"columnClasses\":[%s],\"columnData\":{\"months\":[%s]}}";
    /**
     * Css to style the first column of the table
     */
    protected static final String CSS_ROW_METRIC = "rowMetric";
    /**
     * Css to style a column that refers to a time period
     */
    protected static final String CSS_ROW_TIMEUNIT = "rowTimeUnit";
    /**
     * Css to style a column with a plain value
     */
    protected static final String CSS_ROW_VALUE = "rowValue";
    /**
     * Css to style a column with summary info
     */
    protected static final String CSS_ROW_METRIC_SUMMARY = "rowMetric rowSummary";
    /**
     * Css to style a column with summary info
     */
    protected static final String CSS_ROW_VALUE_SUMMARY = "rowValue rowSummary";
    /**
     * Css to style column with equals image
     */
    protected static final String CSS_ROW_EQUALS_IMAGE = "imageEquals";
    /**
     * Css to style column with more image
     */
    protected static final String CSS_ROW_MORE_IMAGE = "imageMore";
    /**
     * Css to style column with equals image
     */
    protected static final String CSS_ROW_LESS_IMAGE = "imageLess";
    private static int STOCK_COLUMNS = 7;
    /**
     * Data (raw value) for each column
     */
    protected Object[] data;
    /**
     * Context required to translate strings (if needed)
     */
    protected Context context;
    /**
     * Title of the row
     */
    private String rowTitle;
    /**
     * List of css classes for each column of the row
     */
    protected List<String> columnClasses;


    public StockRowBuilder(String rowTitle, Context context) {
        this.rowTitle = rowTitle;
        this.context = context;
        this.columnClasses = defineColumnClasses();
        this.data = initData();
        Log.d("test", data.toString());
    }

    /**
     * Defines the css classes for each column of the row
     */
    protected abstract List<String> defineColumnClasses();

    /**
     * Calculates the new value of the column considering given survey + current column value
     *
     * @return New value for the same column
     */
    protected abstract Object updateColumn(Object currentValue, int newValue,
            SurveyStock surveyStock);

    protected Object updateColumn(Object oldValue, int surveyValue,
            SurveyStock surveyStock,
            int position) {
        return null;
    }

    /**
     * Default value for each column
     */
    protected Object defaultValueColumn() {
        return (int) 0;
    }

    /**
     * Inits a list of defaults values (most of times 0 but it could be a
     */
    protected Object[] initData() {
        Object[] data = new Object[Constants.STOCK_HISTORY_SIZE];
        for (int i = 0; i < Constants.STOCK_HISTORY_SIZE; i++) {
            data[i] = defaultValueColumn();
        }
        return data;
    }


    /**
     * Updates row info with the survey
     */
    public void addSurvey(Survey survey) {
        //Null or not sent surveys are not evaluated or surveys with a date from the future
        if (survey == null || survey.getEventDate() == null || survey.getEventDate().after(
                new Date())) {
            return;
        }
        //Update data for each time dimension
        SurveyStock surveyStock = new SurveyStock(survey);
        addSurveyToData(surveyStock);
    }

    /**
     * Builds a JSON that is inyected via JS into the webview
     */
    public String getRowAsJSON() {
        String rowJSON = String.format(ROW_JSON,
                getColumnClassesAsJSON(),
                getDataAsJSON(data)
        );
        return rowJSON;
    }

    /**
     * Turns the list of column classes into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     */
    private String getColumnClassesAsJSON() {
        return convertListStringToJSON(columnClasses);
    }

    /**
     * Turns an array of objects into a list of quoted items (via toString()) adding rowTitle as
     * first item
     */
    private String getDataAsJSON(Object[] data) {
        if (data == null) return "";
        List<String> dataAsString = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        dataAsString.add(this.rowTitle);
        for (int i = 0; i < data.length; i++) {
            if (data[i]==null) continue;
            dataAsString.add(data[i].toString());
        }
        return convertListStringToJSON(dataAsString);
    }

    /**
     * Turns the list of strings into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     */
    private String convertListStringToJSON(List<String> valuesAsString) {
        int numValues = valuesAsString.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numValues; i++) {
            stringBuilder.append("\"" + valuesAsString.get(i) + "\"");
            if (i != (numValues - 1)) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }


    /**
     * Turns an array of objects into a list of capitalized quoted items (via toString()) adding
     * rowTitle as
     * first item
     */
    private String getDataCapitalizedAsJSON(Object[] data) {
        List<String> dataAsString = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        dataAsString.add(this.rowTitle);
        for (int i = 0; i < data.length; i++) {
            dataAsString.add(WordUtils.capitalize(data[i].toString()));
        }
        return convertListStringToJSON(dataAsString);
    }


    /**
     * Updates months data according to given survey
     */
    private void addSurveyToData(SurveyStock surveyStock) {
        int[] surveyValues = surveyStock.getSurveyValues();
        for (int i = 0;i<surveyValues.length;i++ ) {
            //Updates column considering current value + survey
            if (this instanceof StatusRowBuilder) {
                this.data[i] = updateColumn(this.data[i], surveyValues[i], surveyStock, i);
            } else {
                this.data[i] = updateColumn(this.data[i], surveyValues[i], surveyStock);
            }
        }
    }


}
