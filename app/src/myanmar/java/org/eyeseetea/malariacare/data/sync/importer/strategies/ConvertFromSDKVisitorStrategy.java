package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Organisation;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class ConvertFromSDKVisitorStrategy implements IConvertFromSDKVisitorStrategy {

    private final Context mContext;

    public ConvertFromSDKVisitorStrategy(Context context) {
        mContext = context;
    }

    public void visit(EventExtended sdkEventExtended, Survey convertingSurvey) {
        if (convertingSurvey.getProgram().getUid().equals(mContext.getString(
                R.string.stockProgramUID))) {
            convertingSurvey.setType(Constants.SURVEY_ISSUE);
        } else {
            convertingSurvey.setType(Constants.SURVEY_NO_TYPE);
        }
    }

    public static void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
        Organisation organisationUser = null;
        List<Organisation> organisations = Organisation.getAllOrganisations();
        User me = User.getLoggedUser();
        for (Organisation organisation : organisations) {
            if (organisation.getName().equals(categoryOptionGroupExtended.getName())) {
                organisationUser = organisation;
                organisationUser.setUid(categoryOptionGroupExtended.getUid());
                organisationUser.save();
                me.setOrganisation(organisationUser.getId_organisation());
                me.save();
                break;
            }
        }
        if(me.getOrganisation()== null){
            organisationUser = Organisation.getDefaultOrganization();
            organisationUser.setUid(PreferencesState.getInstance().getContext().getString(R.string.category_option_group_matrix_uid));
            organisationUser.save();
            me.setOrganisation(organisationUser.getId_organisation());
            me.save();
        }
    }
}
