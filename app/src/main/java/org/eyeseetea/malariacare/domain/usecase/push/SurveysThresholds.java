package org.eyeseetea.malariacare.domain.usecase.push;

public class SurveysThresholds {
    private int mCount;
    private int mTimeHours;

    public SurveysThresholds(int count, int timeHours) {
        mCount = count;
        mTimeHours = timeHours;
    }

    public int getCount() {
        return mCount;
    }

    public int getTimeHours() {
        return mTimeHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SurveysThresholds that = (SurveysThresholds) o;

        if (mCount != that.mCount) return false;
        return mTimeHours == that.mTimeHours;

    }

    @Override
    public int hashCode() {
        int result = mCount;
        result = 31 * result + mTimeHours;
        return result;
    }

    @Override
    public String toString() {
        return "SurveysThresholds{" +
                "mCount=" + mCount +
                ", mTimeHours=" + mTimeHours +
                '}';
    }
}
