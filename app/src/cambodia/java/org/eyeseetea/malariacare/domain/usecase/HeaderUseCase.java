package org.eyeseetea.malariacare.domain.usecase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import org.eyeseetea.malariacare.layout.adapters.dashboard.strategies.IHeaderCaseUse;

/**
 * Created by idelcano on 05/12/2016.
 */

public class HeaderUseCase implements IHeaderCaseUse {

    /**
     * Singleton reference
     */
    private static HeaderUseCase instance;

    public static HeaderUseCase getInstance() {
        if (instance == null) {
            instance = new HeaderUseCase();
        }
        return instance;
    }

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return inflater.inflate(headerLayout, null, false);
    }

    @Override
    public void init(Activity activity, int tabTag) {

    }

    @Override
    public void hideHeader(Activity activity) {
    }
}