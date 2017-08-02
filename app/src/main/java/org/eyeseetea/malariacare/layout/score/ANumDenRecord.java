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
package org.eyeseetea.malariacare.layout.score;

import org.eyeseetea.malariacare.data.database.model.QuestionDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ANumDenRecord {

    private Map<QuestionDB, List<Float>> numDenRecord = new HashMap<QuestionDB, List<Float>>();

    public void addRecord(QuestionDB questionDB, Float num, Float den) {
        numDenRecord.put(questionDB, new ArrayList<Float>(Arrays.asList(num, den)));
    }

    public void deleteRecord(QuestionDB questionDB) {
        getNumDenRecord().remove(questionDB);
    }

    public Map<QuestionDB, List<Float>> getNumDenRecord() {
        return numDenRecord;
    }

    public List<Float> calculateNumDenTotal(List<Float> numDenTotal) {
        Float num = numDenTotal.get(0);
        Float den = numDenTotal.get(1);
        for (List<Float> numDen : getNumDenRecord().values()) {
            num += numDen.get(0);
            den += numDen.get(1);
        }
        return new ArrayList<Float>(Arrays.asList(num, den));

    }

}
