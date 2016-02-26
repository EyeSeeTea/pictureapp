package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.MonitorTableBuilder;
import org.eyeseetea.malariacare.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.SuspectedRowBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the structure of the first table in the monitor
 * Created by arrizabalaga on 25/02/16.
 */
public class SuspectedPositiveTableBuilder extends MonitorTableBuilder{

    public SuspectedPositiveTableBuilder(Context context){
        super(context,context.getString(R.string.monitor_table_title_suspected));
    }
    @Override
    protected List<MonitorRowBuilder> defineRowBuilders() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new SuspectedRowBuilder(context));
        rowBuilders.add(new PositiveRowBuilder(context));
        rowBuilders.add(new PfRowBuilder(context));
        rowBuilders.add(new PvRowBuilder(context));
        rowBuilders.add(new PfPvRowBuilder(context));
        rowBuilders.add(new NegativeRowBuilder(context));
        return rowBuilders;
    }
}
