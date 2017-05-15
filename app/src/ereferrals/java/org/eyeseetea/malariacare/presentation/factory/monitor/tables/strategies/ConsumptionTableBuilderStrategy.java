package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;

import java.util.List;

public class ConsumptionTableBuilderStrategy implements IConsumptionTableBuilderStrategy {

    private Context mContext;

    public ConsumptionTableBuilderStrategy(Context context) {
        mContext = context;
    }

    public List<MonitorRowBuilder> defineRows() {
        return null;
    }
}
