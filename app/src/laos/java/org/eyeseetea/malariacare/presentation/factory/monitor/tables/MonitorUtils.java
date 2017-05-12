package org.eyeseetea.malariacare.presentation.factory.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x1RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x2RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x3RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACT6x4RowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ACTStockoutRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTStockoutRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTTestingRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.SuspectedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.TestedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.TreatmentRowBuilder;

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
        //rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new RDTRowBuilder(context));
        rowBuilders.add(new ACT6x1RowBuilder(context));
        rowBuilders.add(new ACT6x2RowBuilder(context));
        rowBuilders.add(new ACT6x3RowBuilder(context));
        rowBuilders.add(new ACT6x4RowBuilder(context));
        return rowBuilders;
    }

    public List<MonitorRowBuilder> defineSuspectedRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        //rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new SuspectedRowBuilder(context));
        rowBuilders.add(new TestedRowBuilder(context));
        rowBuilders.add(new PositiveRowBuilder(context));
        rowBuilders.add(new PfRowBuilder(context));
        rowBuilders.add(new PvRowBuilder(context));
        rowBuilders.add(new PfPvRowBuilder(context));
        rowBuilders.add(new NegativeRowBuilder(context));
        rowBuilders.add(new PositivityRateRowBuilder(context));
        rowBuilders.add(new RDTTestingRowBuilder(context));
        rowBuilders.add(new TreatmentRowBuilder(context));
        rowBuilders.add(new RDTStockoutRowBuilder(context));
        rowBuilders.add(new ACTStockoutRowBuilder(context));
        return rowBuilders;
    }
}
