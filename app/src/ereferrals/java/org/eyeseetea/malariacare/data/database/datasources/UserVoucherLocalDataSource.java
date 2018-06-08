package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserVoucherDBRepository;
import org.eyeseetea.malariacare.domain.entity.intent.UserVoucher;

public class UserVoucherLocalDataSource implements IUserVoucherDBRepository {

    private Context mContext;

    public UserVoucherLocalDataSource(Context context) {
        mContext = context;
    }

    @Override
    public UserVoucher createUserVoucherFromEventUId(String surveyUId) {
        SurveyDB surveyDB = SurveyDB.findByEventUId(surveyUId);
        if(surveyDB==null){
            return null;
        }
        String firstName = "";
        String lastName = "";
        for(ValueDB value : surveyDB.getValueDBs()){
            if(value.getQuestionDB()!=null){
                if(value.getQuestionDB().getUid().equals(mContext.getString(R.string.element_first_name_uid))){
                    firstName = value.getValue();
                }
                }else if(value.getQuestionDB().getUid().equals(mContext.getString(R.string.element_last_name_uid))) {
                lastName = value.getValue();
            }

        }
        return new UserVoucher(surveyDB.getEventUid(), firstName + lastName);
    }
}
