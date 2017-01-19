package org.eyeseetea.malariacare.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.presentation.factory.stock.StockBuilder;

import java.util.List;

/**
 * Created by manuel on 26/12/16.
 */

public class StockService extends IntentService {

    /**
     * Constant added to the intent in order to reuse the service for different 'methods'
     */
    public static final String SERVICE_METHOD = "serviceMethod";

    /**
     * Name of 'prepare stock' action
     */
    public static final String PREPARE_STOCK_DATA =
            "org.eyeseetea.malariacare.services.StockService.PREPARE_STOCK_DATA";

    /**
     * Tag for logging
     */
    public static final String TAG = ".StockService";

    /**
     * Constructor required due to a error message in AndroidManifest.xml if it is not present
     */
    public StockService() {
        super(StockService.class.getSimpleName());
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public StockService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Bad action -> done
        if (!PREPARE_STOCK_DATA.equals(intent.getStringExtra(SERVICE_METHOD))) {
            return;
        }
        prepareStockData();
    }

    private void prepareStockData() {
        Log.i(TAG, "Preparing stock data...");

        //Take last 6 months sent surveys in order to create monitor stats on top of them.
        List<Survey> sentSurveysForStock = Survey.findSentSurveysAfterDate(
                TimePeriodCalculator.getInstance().getMinDateForMonitor());

        Log.i(TAG, String.format("Found %d surveys to build monitor info, aggregating data...",
                sentSurveysForStock.size()));

        StockBuilder stockBuilder = new StockBuilder(getApplicationContext());
        stockBuilder.addSurveys(sentSurveysForStock);

        //Since intents does NOT admit NON serializable as values we use Session instead

        Log.i(TAG, String.format("Stock data calculated ok", sentSurveysForStock.size()));
        Session.putServiceValue(PREPARE_STOCK_DATA, stockBuilder);
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(PREPARE_STOCK_DATA));
    }

}
