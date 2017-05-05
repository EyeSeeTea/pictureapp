package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idelcano on 21/07/2016.
 */
public class MonitorUtils {

    public MonitorUtils(Context context) {
    }

    public List<MonitorRowBuilder> defineRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
//        rowBuilders.add(new PeriodRowBuilder(context, R.string.consumption));
        return rowBuilders;
    }

    public List<MonitorRowBuilder> defineSuspectedRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
//        rowBuilders.add(new PeriodRowBuilder(context, R.string.monitoring_case_stats));
//        rowBuilders.add(new TestedRowBuilder(context));
//        rowBuilders.add(new PfRowBuilder(context));
//        rowBuilders.add(new PvRowBuilder(context));
//        rowBuilders.add(new PfPvRowBuilder(context));
//        rowBuilders.add(new NegativeRowBuilder(context));
//        rowBuilders.add(new PositivityRateRowBuilder(context));
//        rowBuilders.add(new ReferralOptionRowBuilder(context));
        return rowBuilders;
    }
}
