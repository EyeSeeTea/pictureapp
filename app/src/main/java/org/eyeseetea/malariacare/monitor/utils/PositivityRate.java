package org.eyeseetea.malariacare.monitor.utils;

/**
 * POJO that represents a PositivityRate for the monitor
 * Created by arrizabalaga on 26/02/16.
 */
public class PositivityRate {

    /**
     * Counter for the num of positive cases
     */
    int numPositive;

    /**
     * Counter for the num of suspected cases (positive, negative, not tested)
     */
    int numSuspected;

    public PositivityRate(){
    }

    public void incNumPositive(){
        numPositive++;
    }

    public void incNumSuspected(){
        numSuspected++;
    }

    public String toString(){
        if(numSuspected==0){
            return "0%";
        }

        float rate=((float)numPositive/numSuspected)*100;
        return String.format("%.0f%%",rate);
    }

}
