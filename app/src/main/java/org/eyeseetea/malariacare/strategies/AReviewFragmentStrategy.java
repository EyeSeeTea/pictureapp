package org.eyeseetea.malariacare.strategies;

import android.widget.TableRow;

import org.eyeseetea.malariacare.database.model.Value;

public abstract class AReviewFragmentStrategy {

    public abstract TableRow createViewRow(TableRow rowView, Value value);
}
