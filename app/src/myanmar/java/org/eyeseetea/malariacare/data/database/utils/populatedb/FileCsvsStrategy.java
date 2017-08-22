package org.eyeseetea.malariacare.data.database.utils.populatedb;

import java.util.Arrays;
import java.util.List;

public class FileCsvsStrategy extends AFileCsvsStrategy {
    protected static final List<String> csvsToCreate = Arrays.asList(
            PopulateDB.STRING_KEY_CSV,
            PopulateDB.TRANSLATION_CSV,
            PopulateDB.PROGRAMS_CSV,
            PopulateDB.TABS_CSV,
            PopulateDB.HEADERS_CSV,
            PopulateDB.ANSWERS_CSV,
            PopulateDB.OPTION_ATTRIBUTES_CSV,
            PopulateDB.OPTIONS_CSV,
            PopulateDB.QUESTIONS_CSV,
            PopulateDB.QUESTION_RELATIONS_CSV,
            PopulateDB.MATCHES,
            PopulateDB.QUESTION_OPTIONS_CSV,
            PopulateDB.QUESTION_THRESHOLDS_CSV,
            PopulateDB.DRUGS_CSV,
            PopulateDB.PARTNER_CSV,
            PopulateDB.TREATMENT_CSV,
            PopulateDB.DRUG_COMBINATIONS_CSV,
            PopulateDB.TREATMENT_MATCHES_CSV,
            PopulateDB.ORG_UNIT_CSV,
            PopulateDB.ORG_UNIT_LEVEL_CSV
    );
}
