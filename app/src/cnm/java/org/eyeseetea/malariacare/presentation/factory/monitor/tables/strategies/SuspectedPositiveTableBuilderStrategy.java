package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ReferralRowBuilder;

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
        //TODO: comment rows because not compile merging change dbmodels PR
        //https://github.com/EyeSeeTea/pictureapp/pull/1355

        //rowBuilders.add(new PeriodRowBuilder(mContext));
        //rowBuilders.add(new SuspectedRowBuilder(mContext));
        rowBuilders.add(new PositiveRowBuilder(mContext));
        //rowBuilders.add(new PfRowBuilder(mContext));
        rowBuilders.add(new PvRowBuilder(mContext));
        rowBuilders.add(new PfPvRowBuilder(mContext));
        rowBuilders.add(new ReferralRowBuilder(mContext));
        //rowBuilders.add(new NegativeRowBuilder(mContext));
        //rowBuilders.add(new NotTestedRowBuilder(mContext));
        //rowBuilders.add(new PositivityRateRowBuilder(mContext));
        return rowBuilders;
    }
}
