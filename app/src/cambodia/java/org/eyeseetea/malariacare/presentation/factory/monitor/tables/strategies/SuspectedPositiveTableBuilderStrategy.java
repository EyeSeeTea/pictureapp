package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NotTestedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ReferralRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.SuspectedRowBuilder;

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
        rowBuilders.add(new PeriodRowBuilder(mContext));
        rowBuilders.add(new SuspectedRowBuilder(mContext));
        rowBuilders.add(new PositiveRowBuilder(mContext));
        rowBuilders.add(new PfRowBuilder(mContext));
        rowBuilders.add(new PvRowBuilder(mContext));
        rowBuilders.add(new PfPvRowBuilder(mContext));
        rowBuilders.add(new ReferralRowBuilder(mContext));
        rowBuilders.add(new NegativeRowBuilder(mContext));
        rowBuilders.add(new NotTestedRowBuilder(mContext));
        rowBuilders.add(new PositivityRateRowBuilder(mContext));
        return rowBuilders;
    }
}
