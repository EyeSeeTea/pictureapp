package org.eyeseetea.malariacare.strategies;

/**
 * Created by ina on 20/12/2016.
 */

public abstract class AUIMessagesStrategy {
    /**
     * Singleton reference
     */
    private static UIMessagesStrategy instance;

    public static UIMessagesStrategy getInstance() {
        if (instance == null) {
            instance = new UIMessagesStrategy();
        }
        return instance;
    }

    public abstract void showCompulsoryUnansweredToast();
}
