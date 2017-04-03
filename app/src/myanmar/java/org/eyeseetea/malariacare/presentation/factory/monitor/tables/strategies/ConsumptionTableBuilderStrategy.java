package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT12RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT18RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT24RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.CqRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PqRowBuilder;
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
        rowBuilders.add(new PeriodRowBuilder(mContext, R.string.consumption));
        rowBuilders.add(new RDTRowBuilder(mContext));
        rowBuilders.add(new ACT6RowBuilder(mContext));
        rowBuilders.add(new ACT12RowBuilder(mContext));
        rowBuilders.add(new ACT18RowBuilder(mContext));
        rowBuilders.add(new ACT24RowBuilder(mContext));
        rowBuilders.add(new CqRowBuilder(mContext));
        rowBuilders.add(new PqRowBuilder(mContext));
        return rowBuilders;
    }
}
