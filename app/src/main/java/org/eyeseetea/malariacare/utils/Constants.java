package org.eyeseetea.malariacare.utils;

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
    //TODO now review this constants
            DROPDOWN_LIST_DISABLED = 10,
            IMAGES_2 = 10,
            IMAGES_4 = 11,
            IMAGES_6 = 12,
            PHONE = 13,
            IMAGES_3 = 14;


    public static final String DEFAULT_SELECT_OPTION = "";

    public static final int MAX_INT_CHARS = 2;

    public static final int MAX_INT_AGE = 99;

    // ############# TAB TYPE ###############
    public static final int TAB_AUTOMATIC = 0,
            TAB_AUTOMATIC_NON_SCORED = 1,
            TAB_COMPOSITE_SCORE = 2,
            TAB_SCORE_SUMMARY = 4,
            TAB_ADHERENCE = 6,
            TAB_IQATAB=7,
            TAB_REPORTING=8,
            TAB_DYNAMIC_AUTOMATIC_TAB=9;

    //FIXME So far the special sub type of composite scores is treated by name
    public static final String COMPOSITE_SCORE_TAB_NAME="Composite Scores";

    public static final String LABEL="Label";

    // ############# SURVEY STATUS ###############
    public static final int SURVEY_IN_PROGRESS = 0,
            SURVEY_COMPLETED = 1,
            SURVEY_SENT = 2,
            SURVEY_HIDE = 3;

    //############# OPERATION TYPE ##############
    public static final int OPERATION_TYPE_MATCH = 0,
            OPERATION_TYPE_PARENT = 1,
            OPERATION_TYPE_OTHER = 2;

    public static final String FONTS_XSMALL = "xsmall",
            FONTS_SMALL = "small",
            FONTS_MEDIUM = "medium",
            FONTS_LARGE = "large",
            FONTS_XLARGE = "xlarge",
            FONTS_SYSTEM = "system";

    public static final int MAX_ITEMS_IN_DASHBOARD=5;

    //############# LOGIN AUTHORIZATION ACTIONS ##############
    public static final int AUTHORIZE_PUSH = 0,
            AUTHORIZE_PULL = 1;

    public static final String CHECKBOX_YES_OPTION="Yes";

    public static final String QUESTION_OPTION_IDX="QuestionOption_id_question",
        QUESTION_RELATION_IDX="QuestionRelation_operation",
        MATCH_IDX="Match_id_question_relation",
        VALUE_IDX="Value_id_survey";

    public static final String DHIS_API_SERVER="2.20",
            DHIS_SDK_221_SERVER="2.21",
            DHIS_SDK_222_SERVER="2.22";

    /**
     * Max columns for the monitor activity (6 months by default)
     */
    public static final int MONITOR_HISTORY_SIZE=6;

    /**
     * Intent extra param that states that the login is being done due to an attempt to change the server
     */
    public static final int REQUEST_CODE_ON_EULA_ACCEPTED=1;
}
