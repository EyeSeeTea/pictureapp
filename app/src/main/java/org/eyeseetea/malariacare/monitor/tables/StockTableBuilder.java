package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.MonitorTableBuilder;
import org.eyeseetea.malariacare.monitor.rows.ASMQRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.DHAPIP1RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.DHAPIP2RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.DHAPIP3RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.RDTRowBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the structure of the second table in the monitor
 * Created by arrizabalaga on 25/02/16.
 */
public class StockTableBuilder extends MonitorTableBuilder{

    public StockTableBuilder(Context context){
        super(context,context.getString(R.string.monitor_table_title_stock));
    }
    @Override
    protected List<MonitorRowBuilder> defineRowBuilders() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new ASMQRowBuilder(context));
        rowBuilders.add(new DHAPIP1RowBuilder(context));
        rowBuilders.add(new DHAPIP2RowBuilder(context));
        rowBuilders.add(new DHAPIP3RowBuilder(context));
        rowBuilders.add(new RDTRowBuilder(context));
        return rowBuilders;
    }
}
