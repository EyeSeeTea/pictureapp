package org.eyeseetea.malariacare.data.database.convert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.mappers.OptionConvertFromDomainVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.junit.Before;
import org.junit.Test;

public class OptionConvertFromDomainVisitorShould {

    OptionConvertFromDomainVisitor converter;

    @Before
    public void setUp() throws Exception {
        converter = new OptionConvertFromDomainVisitor();
    }

    @Test
    public void convert_a_domain_option_to_db_option() throws Exception {

        Option domainOption = givenADomainOption();

        OptionDB optionDBToEvaluate = converter.visit(domainOption);

        assertEqual(optionDBToEvaluate, givenAValidBDOption());

    }


    private Option givenADomainOption() {

        return Option
                .newBuilder()
                .code("FPL")
                .name("common_option_program_familyPlanning")
                .build();
    }

    private OptionDB givenAValidBDOption() {
        OptionDB optionDB = new OptionDB();
        optionDB.setCode("FPL");
        optionDB.setName("common_option_program_familyPlanning");

        return optionDB;
    }

    private void assertEqual(OptionDB optionDBToEvaluate, OptionDB
            expectedOptionDB) {

        assertThat(optionDBToEvaluate.getCode(), is(expectedOptionDB.getCode()));
        assertThat(optionDBToEvaluate.getName(), is(expectedOptionDB.getName()));
    }
}