package org.eyeseetea.malariacare.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {

    // ############# QUESTION TYPE ###############
    public static final int DROPDOWN_LIST = 1,
            INT = 2,
            LONG_TEXT = 3,
            SHORT_TEXT = 4,
            DATE = 5,
            POSITIVE_INT = 6,
            NO_ANSWER = 7,
            RADIO_GROUP_HORIZONTAL = 8,
            RADIO_GROUP_VERTICAL = 9,
            DROPDOWN_LIST_DISABLED = 10,
            IMAGES_2 = 10,
            IMAGES_4 = 11,
            IMAGES_6 = 12,
            PHONE = 13,
            IMAGES_3 = 14,
            IMAGES_5 = 15,
            COUNTER = 16,
            WARNING = 17,
            REMINDER = 18,
            DROPDOWN_OU_LIST = 19,
            IMAGE_3_NO_DATAELEMENT = 20,
            HIDDEN = 21,
            SWITCH_BUTTON = 22,
            QUESTION_LABEL = 23;


    public static final List<Integer> QUESTION_TYPES_WITH_OPTIONS = Arrays.asList(
            DROPDOWN_LIST,
            RADIO_GROUP_HORIZONTAL,
            RADIO_GROUP_VERTICAL,
            DROPDOWN_LIST_DISABLED,
            DROPDOWN_OU_LIST,
            IMAGES_2,
            IMAGES_3,
            IMAGES_4,
            IMAGES_5,
            IMAGES_6,
            IMAGE_3_NO_DATAELEMENT);


    public static final String DEFAULT_SELECT_OPTION = "";

    public static final int MAX_INT_CHARS = 2;

    public static final int MAX_INT_AGE = 99;

    // ############# TAB TYPE ###############
    public static final int TAB_AUTOMATIC = 0,
            TAB_AUTOMATIC_NON_SCORED = 1,
            TAB_COMPOSITE_SCORE = 2,
            TAB_SCORE_SUMMARY = 4,
            TAB_ADHERENCE = 6,
            TAB_IQATAB = 7,
            TAB_REPORTING = 8,
            TAB_DYNAMIC_AUTOMATIC_TAB = 9,
            TAB_MULTI_QUESTION = 10;

    //FIXME So far the special sub type of composite scores is treated by name
    public static final String COMPOSITE_SCORE_TAB_NAME = "Composite Scores";

    public static final String LABEL = "Label";

    // ############# SURVEY STATUS ###############
    public static final int SURVEY_IN_PROGRESS = 0,
            SURVEY_COMPLETED = 1,
            SURVEY_SENT = 2,
            SURVEY_HIDE = 3,
            SURVEY_CONFLICT = 4,
            SURVEY_QUARANTINE = 5,
            SURVEY_SENDING = 6;

    public static final String FONTS_XSMALL = "xsmall",
            FONTS_SMALL = "small",
            FONTS_MEDIUM = "medium",
            FONTS_LARGE = "large",
            FONTS_XLARGE = "xlarge",
            FONTS_SYSTEM = "system";

    public static final int MAX_ITEMS_IN_DASHBOARD = 5;

    //############# LOGIN AUTHORIZATION ACTIONS ##############
    public static final int AUTHORIZE_PUSH = 0,
            AUTHORIZE_PULL = 1;

    public static final String CHECKBOX_YES_OPTION = "Yes";

    public static final String QUESTION_OPTION_QUESTION_IDX = "QuestionOption_id_question",
            QUESTION_OPTION_MATCH_IDX = "QuestionOption_id_match",
            QUESTION_RELATION_OPERATION_IDX = "QuestionRelation_operation",
            QUESTION_RELATION_QUESTION_IDX = "QuestionRelation_id_question",
            MATCH_QUESTION_RELATION_IDX = "Match_id_question_relation",
            QUESTION_THRESHOLDS_QUESTION_IDX = "QuestionThreshold_id_question",
            VALUE_IDX = "Value_id_survey";

    public static final String DHIS_API_SERVER = "2.20",
            DHIS_SDK_221_SERVER = "2.21",
            DHIS_SDK_222_SERVER = "2.22";

    /**
     * Max columns for the monitor activity (6 months by default)
     */
    public static final int MONITOR_HISTORY_SIZE = 6;

    /**
     * Intent extra param that states that the login is being done due to an attempt to change the server
     */
    public static final int REQUEST_CODE_ON_EULA_ACCEPTED = 1;
}
