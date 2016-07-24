package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;

import java.util.List;

/**
 * Created by nacho on 24/07/16.
 */
public abstract class AMonitorUtils {
    Context context;
    public AMonitorUtils(Context context) {
        this.context=context;
    }

    public abstract List<MonitorRowBuilder> defineRows();

    public abstract List<MonitorRowBuilder> defineSuspectedRows();
}
