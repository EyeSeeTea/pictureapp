package org.eyeseetea.malariacare.presentation.factory.monitor.tables.strategies;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;

import java.util.List;

public interface ISuspectedPositiveTableBuilderStrategy {
    List<MonitorRowBuilder> defineSuspectedRows();
}
