package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.MonitorTableBuilder;

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
        //TODO define rows for this table
        return new ArrayList<>();
    }
}
