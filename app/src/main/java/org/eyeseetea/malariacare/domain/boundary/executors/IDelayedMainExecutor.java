package org.eyeseetea.malariacare.domain.boundary.executors;

public interface IDelayedMainExecutor {
    void postDelayed(Runnable runnable, long delayMillis);
}
