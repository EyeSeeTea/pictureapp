package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import org.eyeseetea.malariacare.database.model.Survey;

/**
 * Created by idelcano on 05/12/2016.
 */

public interface IHeaderCaseUse {
    void init(Activity activity, int tabTag);
    void hideHeader(Activity activity);
    View loadHeader(Integer headerLayout, LayoutInflater inflater);
}
