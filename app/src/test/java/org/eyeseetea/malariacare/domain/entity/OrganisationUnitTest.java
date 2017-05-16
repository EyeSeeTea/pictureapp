package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class OrganisationUnitTest {


    @Test
    public void organisationunit_isbanned_after_ban() {
        OrganisationUnit organisationUnit = new OrganisationUnit("", "", false);
        organisationUnit.ban();
        assertThat(organisationUnit.isBanned(), is(true));
    }

    @Test
    public void organisationunit_isbanned_constructor_banned() {
        OrganisationUnit organisationUnit = new OrganisationUnit("", "", true);
        assertThat(organisationUnit.isBanned(), is(true));
    }

    @Test
    public void organisationunit_no_banned_constructor_no_banned() {
        OrganisationUnit organisationUnit = new OrganisationUnit("", "", false);
        assertThat(organisationUnit.isBanned(), is(false));
    }

    @Test
    public void organisationunit_banned_constructor_previous_date() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date date = calendar.getTime();
        OrganisationUnit organisationUnit = new OrganisationUnit("", "", "", date);
        assertThat(organisationUnit.isBanned(), is(true));
    }

    @Test
    public void organisationunit_no_banned_constructor_future_date() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 5);
        Date date = calendar.getTime();
        OrganisationUnit organisationUnit = new OrganisationUnit("", "", "", date);
        assertThat(organisationUnit.isBanned(), is(false));
    }

}
