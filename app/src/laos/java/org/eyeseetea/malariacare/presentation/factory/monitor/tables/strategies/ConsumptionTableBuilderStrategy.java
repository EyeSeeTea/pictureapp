package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x1RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x2RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x3RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x4RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTRowBuilder;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionTableBuilderStrategy implements IConsumptionTableBuilderStrategy {

    private Context mContext;

    public ConsumptionTableBuilderStrategy(Context context) {
        mContext = context;
    }

    public List<MonitorRowBuilder> defineRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new RDTRowBuilder(mContext));
        rowBuilders.add(new ACT6x1RowBuilder(mContext));
        rowBuilders.add(new ACT6x2RowBuilder(mContext));
        rowBuilders.add(new ACT6x3RowBuilder(mContext));
        rowBuilders.add(new ACT6x4RowBuilder(mContext));
        return rowBuilders;
    }
}
