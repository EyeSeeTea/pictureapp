package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ASMQRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.DHAPIPRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;
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
        rowBuilders.add(new PeriodRowBuilder(mContext));
        rowBuilders.add(new ASMQRowBuilder(mContext));
        rowBuilders.add(new DHAPIPRowBuilder(mContext));
        rowBuilders.add(new RDTRowBuilder(mContext));
        return rowBuilders;
    }
}
