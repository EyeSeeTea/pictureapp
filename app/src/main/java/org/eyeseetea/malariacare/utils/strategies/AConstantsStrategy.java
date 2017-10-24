package org.eyeseetea.malariacare.utils.strategies;

import org.eyeseetea.malariacare.utils.Constants;

import java.util.Arrays;
import java.util.List;

public abstract class AConstantsStrategy {

    public static final List<Integer> QUESTION_TYPES_NO_DATA_ELEMENT = Arrays.asList(
            Constants.DROPDOWN_OU_LIST,
            Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT,
            Constants.REMINDER,
            Constants.QUESTION_LABEL,
            Constants.DYNAMIC_STOCK_IMAGE_RADIO_BUTTON,
            Constants.WARNING
    );

}
