package org.eyeseetea.malariacare.strategies;

import android.widget.TableRow;

import org.eyeseetea.malariacare.domain.entity.Value;

public abstract class AReviewScreenAdapterStrategy {

    public abstract TableRow createViewRow(TableRow rowView, Value value);
}
