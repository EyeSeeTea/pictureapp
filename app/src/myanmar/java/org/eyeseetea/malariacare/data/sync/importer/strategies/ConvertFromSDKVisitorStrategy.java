package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.PartnerDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
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

    public void visit(EventExtended sdkEventExtended, SurveyDB convertingSurvey) {
        if (convertingSurvey.getProgramDB().getUid().equals(mContext.getString(
                R.string.stockProgramUID))) {
            convertingSurvey.setType(Constants.SURVEY_ISSUE);
        } else {
            convertingSurvey.setType(Constants.SURVEY_NO_TYPE);
        }
    }

    public static void visit(CategoryOptionGroupExtended categoryOptionGroupExtended) {
        PartnerDB organisationUser = null;
        List<PartnerDB> partnerDBs = PartnerDB.getAllOrganisations();
        UserDB me = UserDB.getLoggedUser();
        for (PartnerDB partnerDB : partnerDBs) {
            if (partnerDB.getName().equals(categoryOptionGroupExtended.getName())) {
                organisationUser = partnerDB;
                organisationUser.setUid(categoryOptionGroupExtended.getUid());
                organisationUser.save();
                me.setOrganisation(organisationUser.getId_partner());
                me.save();
                break;
            }
        }
        if (me.getOrganisation() == 0) {
            organisationUser = PartnerDB.getDefaultOrganization();
            organisationUser.setUid(PreferencesState.getInstance().getContext().getString(
                    R.string.category_option_group_matrix_uid));
            organisationUser.save();
            me.setOrganisation(organisationUser.getId_partner());
            me.save();
        }
    }
}
