package org.eyeseetea.malariacare.data.remote;

import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.CategoryOptionGroupFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataElementFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitToProgramRelationFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow
        .OrganisationUnitToProgramRelationFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.UserAccountFlow;
import org.hisp.dhis.client.sdk.models.category.CategoryOption;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.util.ArrayList;
import java.util.List;

public class SdkQueries {

    public static List<String> getAssignedPrograms() {
        //return MetaDataController.getAssignedPrograms();
        List<String> uids = new ArrayList<>();
        List<ProgramFlow> programsFlow = new Select().from(ProgramFlow.class).queryList();
        for (ProgramFlow programFlow : programsFlow) {
            uids.add(programFlow.getUId());
        }
        return uids;
    }

    public static ProgramFlow getProgram(String assignedProgramID) {
        //return MetaDataController.getProgram(assignedProgramID);
        return new Select().from(ProgramFlow.class).where(
                ProgramFlow_Table.uId.eq(assignedProgramID)).querySingle();
    }

    public static List<OptionSetFlow> getOptionSets() {
        return new Select().from(OptionSetFlow.class).queryList();
        //MetaDataController.getOptionSets();
    }

    public static UserAccountFlow getUserAccount() {
        return new Select().from(UserAccountFlow.class).querySingle();
        //return MetaDataController.getUserAccount();
    }

    public static DataElementFlow getDataElement(DataElementFlow dataElement) {
        //return MetaDataController.getDataElement(dataElement.getId());
        return new Select().from(DataElementFlow.class).where(DataElementFlow_Table.uId.
                is(dataElement.getUId())).querySingle();
    }

    public static DataElementFlow getDataElement(String UId) {
        return new Select().from(DataElementFlow.class).where(DataElementFlow_Table.uId.
                is(UId)).querySingle();
    }


    public static List<OrganisationUnitFlow> getAssignedOrganisationUnits() {
        return new Select().from(OrganisationUnitFlow.class)
                .queryList();
    }

    public static List<ProgramFlow> getProgramsForOrganisationUnit(String UId,
            ProgramType... programType) {

        List<OrganisationUnitToProgramRelationFlow> organisationUnitProgramRelationships =
                new Select().from(OrganisationUnitToProgramRelationFlow.class).where(
                        OrganisationUnitToProgramRelationFlow_Table.organisationUnit.
                                is(UId)).queryList();

        List<ProgramFlow> programs = new ArrayList<ProgramFlow>();
        for (OrganisationUnitToProgramRelationFlow oupr : organisationUnitProgramRelationships) {
            if (programType != null) {
                for (ProgramType kind : programType) {
                    List<ProgramFlow> plist = new Select().from(ProgramFlow.class).where(
                            ProgramFlow_Table.id.is(oupr.getProgram().getId()))
                            .and(
                                    ProgramFlow_Table.programType.is(kind)).queryList();
                    programs.addAll(plist);
                }
            }
        }
        return programs;
    }

    public static List<EventFlow> getEvents(String organisationUnitUId, String programUId) {
        return new Select().from(EventFlow.class).where(
                EventFlow_Table.orgUnit.eq(organisationUnitUId))
                .and(EventFlow_Table.program.eq(programUId)).queryList();
    }

    public static Event getEvent(String uId) {
        return D2.events().get(uId).toBlocking().first();
    }

    public static List<TrackedEntityDataValueFlow> getDataValues(String eventUId) {
        return new Select().from(TrackedEntityDataValueFlow.class).where(
                TrackedEntityDataValueFlow_Table.event.eq(eventUId)).queryList();
    }

    public static List<EventFlow> getEvents() {
        return new Select().from(EventFlow.class).queryList();
    }

    public static ProgramStageFlow getProgramStage(ProgramStageFlow programStage) {
        return new Select().from(ProgramStageFlow.class).where(
                ProgramStageFlow_Table.id.is(programStage.getId())).querySingle();
        //return MetaDataController.getProgramStage(programStage);
    }

    public static List<ProgramStageFlow> getProgramStages(ProgramFlow program) {
        return new Select().from(ProgramStageFlow.class).where(
                ProgramStageFlow_Table.program.is(program.getUId()))
                .orderBy(OrderBy.fromProperty(ProgramStageFlow_Table.sortOrder)).queryList();
    }


    public static void createDBIndexes() {

        /*
        new Index<QuestionOption>(Constants.QUESTION_OPTION_QUESTION_IDX).on(QuestionOption.class,
                QuestionOption$Table.ID_QUESTION).enable();
        new Index<QuestionOption>(Constants.QUESTION_OPTION_MATCH_IDX).on(QuestionOption.class,
                QuestionOption$Table.ID_MATCH).enable();

        new Index<QuestionRelation>(Constants.QUESTION_RELATION_OPERATION_IDX).on(
                QuestionRelation.class, QuestionRelation$Table.OPERATION).enable();
        new Index<QuestionRelation>(Constants.QUESTION_RELATION_QUESTION_IDX).on(
                QuestionRelation.class, QuestionRelation$Table.ID_QUESTION).enable();

        new Index<Match>(Constants.MATCH_QUESTION_RELATION_IDX).on(Match.class,
                Match$Table.ID_QUESTION_RELATION).enable();

        new Index<QuestionThreshold>(Constants.QUESTION_THRESHOLDS_QUESTION_IDX).on(
                QuestionThreshold.class, QuestionThreshold$Table.ID_QUESTION).enable();

        new Index<Value>(Constants.VALUE_IDX).on(Value.class, Value$Table.ID_SURVEY).enable();
        */
    }

    public static String getCategoryOptionUIDByCurrentUser() {
        String userName =
                D2.me().userCredentials().toBlocking().single().getUsername().toLowerCase();

        List<CategoryOption> categoryOptions =
                D2.categoryOptions().list().toBlocking().single();

        for (CategoryOption categoryOption : categoryOptions) {
            if (categoryOption.getCode() != null
                    && categoryOption.getCode().toLowerCase().equals(
                    userName)) {
                return categoryOption.getUId();
            }
        }

        return null;
    }

    public static List<CategoryOptionGroupFlow> getCategoryOptionGroups() {
        return new Select().from(CategoryOptionGroupFlow.class)
                .queryList();
    }

}
