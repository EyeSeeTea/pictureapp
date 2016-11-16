package org.eyeseetea.malariacare.services.strategies;


import android.content.Context;

public abstract class APushServiceStrategy {
    public interface Callback{
        void onPushFinished();
        void onPushError(String message);
    }
    protected Context mContext;

    public APushServiceStrategy(Context context){
        mContext = context;
    }

    public abstract void push(Callback callback);
}
