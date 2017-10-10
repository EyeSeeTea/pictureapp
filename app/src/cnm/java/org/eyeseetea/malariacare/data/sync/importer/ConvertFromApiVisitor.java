package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class ConvertFromApiVisitor implements IConvertFromApiVisitor {
    @Override
    public void visit(OrgUnitTree orgUnitTree) {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOutput(
                Constants.DROPDOWN_LIST_OU_TREE);
        for (QuestionDB questionDB : questionDBs) {

            OptionDB optionProb = OptionDB.findByNameAndCode(orgUnitTree.getName_Prov_E(),
                    orgUnitTree.getCode_Prov_T());
            if (optionProb == null) {
                optionProb = new OptionDB(orgUnitTree.getName_Prov_E(),
                        orgUnitTree.getCode_Prov_T(), 0f, questionDB.getAnswerDB());
                optionProb.save();
            }

            OptionDB optionDist = OptionDB.findByNameAndCode(orgUnitTree.getName_Dist_E(),
                    orgUnitTree.getCode_Dist_T() + "");
            if (optionDist == null) {
                optionDist = new OptionDB(orgUnitTree.getName_Dist_E(),
                        orgUnitTree.getCode_Dist_T() + "", 0f, questionDB.getAnswerDB());
                optionDist.setId_parent_fk(optionProb.getId_option());
                optionDist.save();
            }

            OptionDB optionComm = OptionDB.findByNameAndCode(orgUnitTree.getName_Comm_E(),
                    orgUnitTree.getCode_Comm_T() + "");
            if (optionComm == null) {
                optionComm = new OptionDB(orgUnitTree.getName_Comm_E(),
                        orgUnitTree.getCode_Comm_T() + "", 0f, questionDB.getAnswerDB());
                optionComm.setId_parent_fk(optionDist.getId_option());
                optionComm.save();
            }

            OptionDB optionVill = new OptionDB(orgUnitTree.getName_Vill_E(),
                    orgUnitTree.getCode_Vill_T() + "", 0f, questionDB.getAnswerDB());
            optionVill.setId_parent_fk(optionComm.getId_option());
            OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
            optionAttributeDB.setPath(
                    "[" + orgUnitTree.getLat() + "," + orgUnitTree.getLng() + "]");
            optionAttributeDB.save();
            optionVill.setOptionAttributeDB(optionAttributeDB);
            optionVill.save();
        }
    }
}
