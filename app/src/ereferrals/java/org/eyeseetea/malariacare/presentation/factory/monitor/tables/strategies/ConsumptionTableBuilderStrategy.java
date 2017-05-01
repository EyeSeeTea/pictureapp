package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionTableBuilderStrategy implements IConsumptionTableBuilderStrategy {

    private Context mContext;

    public ConsumptionTableBuilderStrategy(Context context) {
        mContext = context;
    }

    public List<MonitorRowBuilder> defineRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(mContext, R.string.consumption));
        return rowBuilders;
    }
}
