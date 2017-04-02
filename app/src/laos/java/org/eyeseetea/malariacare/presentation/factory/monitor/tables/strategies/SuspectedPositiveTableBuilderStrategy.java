package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACTStockoutRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTStockoutRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTTestingRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.SuspectedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.TestedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.TreatmentRowBuilder;

import java.util.ArrayList;
import java.util.List;

public class SuspectedPositiveTableBuilderStrategy implements
        ISuspectedPositiveTableBuilderStrategy {
    private Context mContext;

    public SuspectedPositiveTableBuilderStrategy(Context context) {
        mContext = context;
    }

    public List<MonitorRowBuilder> defineSuspectedRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new SuspectedRowBuilder(mContext));
        rowBuilders.add(new TestedRowBuilder(mContext));
        rowBuilders.add(new PositiveRowBuilder(mContext));
        rowBuilders.add(new PfRowBuilder(mContext));
        rowBuilders.add(new PvRowBuilder(mContext));
        rowBuilders.add(new PfPvRowBuilder(mContext));
        rowBuilders.add(new NegativeRowBuilder(mContext));
        rowBuilders.add(new PositivityRateRowBuilder(mContext));
        rowBuilders.add(new RDTTestingRowBuilder(mContext));
        rowBuilders.add(new TreatmentRowBuilder(mContext));
        rowBuilders.add(new RDTStockoutRowBuilder(mContext));
        rowBuilders.add(new ACTStockoutRowBuilder(mContext));
        return rowBuilders;
    }
}
