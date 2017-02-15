package org.eyeseetea.malariacare.presentation.factory.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ASMQRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.DHAPIPRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NegativeRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.NotTestedRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PeriodRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfPvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PfRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositiveRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PositivityRateRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.PvRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.RDTRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.ReferralRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.rows.SuspectedRowBuilder;

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
        rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new ASMQRowBuilder(context));
        rowBuilders.add(new DHAPIPRowBuilder(context));
        rowBuilders.add(new RDTRowBuilder(context));
        return rowBuilders;
    }

    public List<MonitorRowBuilder> defineSuspectedRows() {
        List<MonitorRowBuilder> rowBuilders = new ArrayList<>();
        rowBuilders.add(new PeriodRowBuilder(context));
        rowBuilders.add(new SuspectedRowBuilder(context));
        rowBuilders.add(new PositiveRowBuilder(context));
        rowBuilders.add(new PfRowBuilder(context));
        rowBuilders.add(new PvRowBuilder(context));
        rowBuilders.add(new PfPvRowBuilder(context));
        rowBuilders.add(new ReferralRowBuilder(context));
        rowBuilders.add(new NegativeRowBuilder(context));
        rowBuilders.add(new NotTestedRowBuilder(context));
        rowBuilders.add(new PositivityRateRowBuilder(context));
        return rowBuilders;
    }
}
