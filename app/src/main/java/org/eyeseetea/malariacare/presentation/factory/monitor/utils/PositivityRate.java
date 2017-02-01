/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eyeseetea.malariacare.presentation.factory.monitor.utils;

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

    public PositivityRate() {
    }

    public void incNumPositive() {
        numPositive++;
    }

    public void incNumSuspected() {
        numSuspected++;
    }

    public String toString() {
        if (numSuspected == 0) {
            return "0%";
        }

        float rate = ((float) numPositive / numSuspected) * 100;
        return String.format("%.0f%%", rate);
    }

}
