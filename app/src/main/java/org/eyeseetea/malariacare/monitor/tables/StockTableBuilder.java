/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eyeseetea.malariacare.monitor.tables;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.MonitorTableBuilder;
import org.eyeseetea.malariacare.monitor.rows.DHAPIPRowBuilder;
import org.eyeseetea.malariacare.monitor.rows.ASMQRowBuilder;
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
        rowBuilders.add(new DHAPIPRowBuilder(context));
        rowBuilders.add(new RDTRowBuilder(context));
        return rowBuilders;
    }
}
