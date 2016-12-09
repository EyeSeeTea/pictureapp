package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.ACT12RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.ACT18RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.ACT24RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.ACT6RowBuilder;
import org.eyeseetea.malariacare.monitor.rows.CqRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PqRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.RDTRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.TestedRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.TreatmentRowBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idelcano on 21/07/2016.
 */
public class MonitorUtils extends AMonitorUtils {

    public MonitorUtils(Context context) {
        super(context);
    }

    public List<MonitorRowBuilder> defineRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(context, R.string.consumption));
        rowBuilders.add(new RDTRowBuilder(context));
        rowBuilders.add(new ACT6RowBuilder(context));
        rowBuilders.add(new ACT12RowBuilder(context));
        rowBuilders.add(new ACT18RowBuilder(context));
        rowBuilders.add(new ACT24RowBuilder(context));
        rowBuilders.add(new CqRowBuilder(context));
        rowBuilders.add(new PqRowBuilder(context));
        return rowBuilders;
    }

    public List<MonitorRowBuilder> defineSuspectedRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(context, R.string.monitoring_case_stats));
        rowBuilders.add(new TestedRowBuilder(context));
        rowBuilders.add(new PfRowBuilder(context));
        rowBuilders.add(new PvRowBuilder(context));
        rowBuilders.add(new PfPvRowBuilder(context));
        rowBuilders.add(new NegativeRowBuilder(context));
        rowBuilders.add(new PositivityRateRowBuilder(context));
        rowBuilders.add(new TreatmentRowBuilder(context));
        return rowBuilders;
    }
}
