package org.eyeseetea.malariacare.layout.adapters.dashboard.strategies;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by idelcano on 05/12/2016.
 */

public class HeaderCaseUse implements IHeaderCaseUse {

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

    public static View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return inflater.inflate(headerLayout, null, false);
    }

    public static void init(View view, int tabTag) {
    }

    public void hideHeader(Activity activity) {
    }