package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ReferralOptionRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.TestedRowBuilder;

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
        rowBuilders.add(new PeriodRowBuilder(mContext, R.string.monitoring_case_stats));
        rowBuilders.add(new TestedRowBuilder(mContext));
        rowBuilders.add(new PfRowBuilder(mContext));
        rowBuilders.add(new PvRowBuilder(mContext));
        rowBuilders.add(new PfPvRowBuilder(mContext));
        rowBuilders.add(new NegativeRowBuilder(mContext));
        rowBuilders.add(new PositivityRateRowBuilder(mContext));
        rowBuilders.add(new ReferralOptionRowBuilder(mContext));
        return rowBuilders;
    }
}
